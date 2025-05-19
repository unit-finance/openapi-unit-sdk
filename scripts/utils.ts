import dotenv from "dotenv";
import fs from "fs";

export function loadDotenv() {
    dotenv.config();
}

export function openFile(path: string, fileType: string): string {
    if (!path.includes(`.${fileType}`)) {
        throw new Error(`Path must lead to a ${fileType} file!`);
    }

    return fs.readFileSync(path, "utf-8");
}

export function getPathCmdParameter(fileType: string, pathArgName = "path"): string {
    const pathArg = process.argv.find((val) => val.includes(`--${pathArgName}=`));

    if (pathArg == null) {
        throw new Error(
            `Undefined path! Don't forget to specify the path, e.g. "...fixFileRelatedGetRequests.ts ./TargetFile.${fileType}"`
        );
    }

    const path = pathArg?.replace(`--${pathArgName}=`, "");
    console.log(pathArgName, path);
    if (!path.includes(`.${fileType}`)) {
        throw new Error(`Path must lead to a ${fileType} file!`);
    }

    return path;
}

export function openJsonFile(path: string): string {
    return openFile(path, "json");
}