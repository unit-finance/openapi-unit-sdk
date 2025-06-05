import fs from "fs";
import { openPythonFile, getPythonPathCmdParameter } from "./pythonUtils";
import { getPathCmdParameter, openJsonFile } from "../utils";
import { BundledSchema } from "../common";

function fixDeepObjectQueryParamsApiClient(data: string): string {
    const apiClientParamSerializerRegex = /_host=None,\s+_request_auth=None/gm;
    const queryParamsConditionalRegex = /query_params,\s+collection_formats/gm;
    const toQueryParamsRegex = /def parameters_to_url_query\(self, params, collection_formats\):/gm;
    const toQueryParamsDocsRegex = /(def parameters_to_url_query[\s\S]*?)(:param dict collection_formats: Parameter collection formats)/gm;
    const toQueryParamsConditionalRegex = /(def parameters_to_url_query[\s\S]*?)(if isinstance\(v, dict\):\s+v = json\.dumps\(v\))/gm;
    const toQueryParamsFormatsRegex = /(def parameters_to_url_query[\s\S]*?)(if k in collection_formats:)/gm;

    let processedData = data;
    
    processedData = processedData.replace(
        apiClientParamSerializerRegex, 
        match => `${match},\n        deep_object_names=None`
    );
    
    processedData = processedData.replace(
        queryParamsConditionalRegex, 
        match => `${match},\n                deep_object_names`
    );
    
    processedData = processedData.replace(
        toQueryParamsRegex, 
        `def parameters_to_url_query(self, params, collection_formats, deep_objects_names):\n`
    );
    
    processedData = processedData.replace(
        toQueryParamsDocsRegex, 
        (_, prefix, docString) => `${prefix}${docString}\n        :param list[str] deep_objects_names: Names of deep objects to properly detect and serialize them`
    );
    
    processedData = processedData.replace(
        toQueryParamsConditionalRegex, 
        (_, prefix) => `${prefix}if k not in deep_objects_names and isinstance(v, dict):\n                v = json.dumps(v)`
    );
    
    processedData = processedData.replace(
        toQueryParamsFormatsRegex, 
        (_, prefix) => `${prefix}if k in deep_objects_names and isinstance(v, dict):
                for key in v:
                    if isinstance(v[key], list):
                        for index, item in enumerate(v[key]):
                            new_params.append((f"{k}[{key}][{index}]", item))
                    else:
                        new_params.append((f"{k}[{key}]", v[key]))
            elif k in collection_formats:`
    );

    return processedData;
}

function operationIdToPythonFunction(operationId: string): string {
    return '_' + operationId.replace(/([A-Z])/g, '_$1').toLowerCase() + '_serialize';
}

function resolveRefParameter(schema: BundledSchema, refPath: string): any {
    const path = refPath.replace('#/', '').split('/');
    
    let current: any = schema;
    for (const segment of path) {
        if (current[segment] === undefined) {
            return null;
        }
        current = current[segment];
    }
    
    return current;
}

function findDeepObjectParameters(openApiData: string): Map<string, string[]> {
    const bundledSchema = JSON.parse(openApiData) as BundledSchema;
    const result = new Map<string, string[]>();
    
    const deepObjectReusableParams = new Set<string>();
    if (bundledSchema.components && bundledSchema.components.parameters) {
        for (const paramName in bundledSchema.components.parameters) {
            const param = bundledSchema.components.parameters[paramName];
            if (param.style === 'deepObject' && param.in === 'query' && param.name) {
                deepObjectReusableParams.add(paramName);
            }
        }
    }
    
    for (const path in bundledSchema.paths) {
        const pathObj = bundledSchema.paths[path];
        
        for (const method in pathObj) {
            const operation = pathObj[method];
            
            if (operation.operationId) {
                const functionName = operationIdToPythonFunction(operation.operationId);
                const deepObjectParams: string[] = [];
                
                if (operation.parameters) {
                    for (const param of operation.parameters) {
                        // Handle direct parameter definition
                        if (param.style === 'deepObject' && param.in === 'query' && param.name) {
                            deepObjectParams.push(param.name);
                        }
                        // Handle $ref to a parameter
                        else if (param.$ref) {
                            const resolvedParam = resolveRefParameter(bundledSchema, param.$ref);
                            if (resolvedParam && resolvedParam.style === 'deepObject' && resolvedParam.in === 'query' && resolvedParam.name) {
                                deepObjectParams.push(resolvedParam.name);
                            }
                        }
                    }
                }
                
                if (deepObjectParams.length > 0) {
                    result.set(functionName, deepObjectParams);
                }
            }
        }
    }
    
    const totalParamCount = Array.from(result.values()).reduce((sum, params) => sum + params.length, 0);
    
    console.log("Found deep object parameters:", Array.from(result.entries()), 
                `\nFunctions with deep objects: ${result.size}, Total parameters: ${totalParamCount}`);
    return result;
}

function fixDeepObjectQueryParamsUnitApi(data: string, openApiData: string): string {
    const deepObjectMap = findDeepObjectParameters(openApiData);
    
    let processedData = data;
    
    deepObjectMap.forEach((paramNames, functionName) => {
        const functionRegex = new RegExp(`def ${functionName}\\([\\s\\S]*?return self\\.api_client\\.param_serialize\\([\\s\\S]*?_host=_host,\\s+_request_auth=_request_auth\\s+\\)`, 'g');
        
        processedData = processedData.replace(functionRegex, (match) => {
            if (match.includes('deep_object_names=')) {
                return match;
            }
            
            const deepObjectNames = JSON.stringify(paramNames);
            return match.replace(/\s+\)$/, `,\n            deep_object_names=${deepObjectNames}\n        )`);
        });
    });
    
    return processedData;
}

function execute() {
    try {
        console.log("Fixing deep object query parameters in api_client.py...");
        const path = getPythonPathCmdParameter();
        const apiClientData = openPythonFile(path);
        const processedData = fixDeepObjectQueryParamsApiClient(apiClientData);
        console.log("Fixed deep object query parameters in api_client.py!");

        console.log("Fixing deep object query parameters in unit api...");
        const schemaPath = getPathCmdParameter("json", "schema");
        const openApiData = openJsonFile(schemaPath);
        const unitApiPath = getPythonPathCmdParameter("unit-api");
        const unitApiData = openPythonFile(unitApiPath);
        const processedUnitApiData = fixDeepObjectQueryParamsUnitApi(unitApiData, openApiData);
        console.log("Fixed deep object query parameters in unit api!");

        fs.writeFileSync(path, processedData);
        fs.writeFileSync(unitApiPath, processedUnitApiData);
    } catch (e) {
        console.error(e);
    }
}

execute();
