import { getPathCmdParameter, loadDotenv, openJavaFile } from "./utils";
import fs from "fs";

function addVersionHeadersToApiClient(data: string): string {
    const interceptorRegex = /interceptor = null;/gm;
    const interceptorWithVersionHeader = `interceptor = (req) -> {
      req.setHeader("X-UNIT-SDK", "unit-node-sdk@v${process.env.API_VERSION}");
    };`;

    const setInterceptorRegex = /this\.interceptor = interceptor;/gm;
    const setInterceptorWithVersionHeader = `this.interceptor = (req) -> {
      req.setHeader("X-UNIT-SDK", "unit-node-sdk@v${process.env.API_VERSION}");
      interceptor.accept(req);
    };`;

    var processedData = data;
    processedData = processedData.replaceAll(
        interceptorRegex,
        interceptorWithVersionHeader
    );

    processedData = processedData.replaceAll(
        setInterceptorRegex,
        setInterceptorWithVersionHeader
    );

    return processedData;
}

function execute() {
    loadDotenv();

    try {
        console.log("Adding version header to ApiClient...");
        const path = getPathCmdParameter();
        const data = openJavaFile(path);
        const processedData = addVersionHeadersToApiClient(data);
        console.log("Added version header to ApiClient!");

        fs.writeFileSync(path, processedData);
    } catch (e) {
        console.error(e);
    }
}

execute();
