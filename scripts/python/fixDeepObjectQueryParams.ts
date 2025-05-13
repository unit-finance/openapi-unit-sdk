import fs from "fs";
import { openPythonFile, getPythonPathCmdParameter } from "./pythonUtils";

function fixDeepObjectQueryParams(data: string): string {
    const apiClientParamSerializerRegex = /_host=None,\s+_request_auth=None/gm;
    const queryParamsConditionalRegex = /query_params,\s+collection_formats/gm;
    const toQueryParamsRegex = /def parameters_to_url_query\(self, params, collection_formats\):/gm;
    const toQueryParamsDocsRegex = /:param dict collection_formats: Parameter collection formats/gm;
    const toQueryParamsConditionalRegex = /if isinstance\(v, \(int, float\)\):\s+v = str\(v\)/gm;
    const toQueryParamsFormatsRegex = /if k in collection_formats:/gm;

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
        match => `${match}\n        :param list[str] deep_objects_names: Names of deep objects to properly detect and serialize them`
    );
    
    processedData = processedData.replace(
        toQueryParamsConditionalRegex, 
        match => `${match}\n            if k not in deep_objects_names and isinstance(v, dict):\n                v = json.dumps(v)`
    );
    
    processedData = processedData.replace(
        toQueryParamsFormatsRegex, 
        `if k in deep_objects_names and isinstance(v, dict):\n                for key in v:\n                    new_params.append((f"{k}[{key}]", v[key]))\n            elif k in collection_formats:`
    );

    return processedData;
}

function execute() {
    try {
        console.log("Fixing deep object query parameters...");
        const path = getPythonPathCmdParameter();
        const apiClientData = openPythonFile(path);
        const processedData = fixDeepObjectQueryParams(apiClientData);
        console.log("Fixed deep object query parameters!");

        fs.writeFileSync(path, processedData);
    } catch (e) {
        console.error(e);
    }
}

execute();
