import { getPathCmdParameter, loadDotenv, openJavaFile } from "./utils";
import fs from "fs";

function addVersionHeadersToApiClient(data: string): string {
    const interceptorRegex = /interceptor = null;/gm;

    var processedData = data;
    console.log("API VERSION: " + process.env.API_VERSION);
    const interceptorWithVersionHeader = `interceptor = (req) -> {
      req.setHeader("X-UNIT-SDK", "unit-node-sdk@v${process.env.API_VERSION}");
    };`;
    processedData = processedData.replaceAll(
        interceptorRegex,
        interceptorWithVersionHeader
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
