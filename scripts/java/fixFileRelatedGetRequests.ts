import fs from "fs";

function processData(data: string): string {
    const mainFunctionNameRegex = /(File )(get|download)/gm;
    const mainFunctionHttpInfoReturnRegex =
        /(ApiResponse<)(File)(> localVarResponse)/gm;
    const httpInfoNameRegex = /(ApiResponse<)(File)(> get|> download)/gm;
    const httpInfoReturnConstructorRegex =
        /(return new ApiResponse<)(File)(>)/gm;
    const httpInfoReturnRegex =
        /localVarResponse\.body\(\) == null \? null : memberVarObjectMapper\.readValue\(localVarResponse\.body\(\), new TypeReference<File>\(\) {}\) \/\/ closes the InputStream/gm;
    const httpInfoReturnStringRegex =
        /localVarResponse\.body\(\) == null \? null : memberVarObjectMapper\.readValue\(localVarResponse\.body\(\), new TypeReference<String>\(\) {}\) \/\/ closes the InputStream/gm;

    let processedData = data;

    processedData = processedData.replace(
        mainFunctionNameRegex,
        (_match, _returnType, beginningOfName) => {
            return "InputStream " + beginningOfName;
        }
    );

    processedData = processedData.replace(
        mainFunctionHttpInfoReturnRegex,
        (_match, beginningOfType, _genericType, beginningOfName) => {
            return beginningOfType + "InputStream" + beginningOfName;
        }
    );

    processedData = processedData.replace(
        httpInfoNameRegex,
        (_match, beginningOfType, _genericType, beginningOfName) => {
            return beginningOfType + "InputStream" + beginningOfName;
        }
    );

    processedData = processedData.replace(
        httpInfoReturnConstructorRegex,
        (
            _match,
            beginningOfConstructorCall,
            _genericType,
            endOfConstructoCall
        ) => {
            return (
                beginningOfConstructorCall + "InputStream" + endOfConstructoCall
            );
        }
    );

    processedData = processedData.replace(httpInfoReturnRegex, () => {
        return "localVarResponse.body() == null ? null : localVarResponse.body()";
    });

    processedData = processedData.replace(httpInfoReturnStringRegex, () => {
        return "localVarResponse.body() == null ? null : IOUtils.toString(localVarResponse.body(), StandardCharsets.UTF_8)";
    });

    processedData = processedData.replace(
        "import org.apache.http.HttpEntity;",
        `import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;`
    );

    processedData = processedData.replace(
        "import java.net.URI;",
        `import java.nio.charset.StandardCharsets;
import java.net.URI;`
    );

    return processedData;
}

function execute() {
    const pathArg = process.argv.find((val) => val.includes("--path="));
    if (pathArg == null) {
        console.error(
            `Undefined path! Don't forget to specify the path, e.g. "...fixFileRelatedGetRequests.ts ./TargetFile.java"`
        );
        return;
    }

    const path = pathArg?.replace("--path=", "");
    if (!path.includes(".java")) {
        console.error("Path must lead to a java file!");
        return;
    }

    try {
        console.log("Fixing file related get requests...");
        const data = fs.readFileSync(path, "utf-8");
        const processedData = processData(data);
        fs.writeFileSync(path, processedData);
        console.log("Fixed file related get requests!");
    } catch (e) {
        console.error(e);
    }
}

execute();
