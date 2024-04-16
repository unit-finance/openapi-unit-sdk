import fs from 'fs';

const REF_DEFINITIONS_REGEX = /\/definitions/gm;
const DEFINITIONS_REGEX = /("definitions":\s*{)((.|\n|\r)*)(})/;

const replaceDefinitionsWithComponents = (filePath: string) => {
    const fileContents = fs.readFileSync(filePath, { encoding: 'utf-8' });

    const stringWithReplacedDefinitions = fileContents.replace(
        DEFINITIONS_REGEX,
        (_, __, p2: string) => {
            const indentedContent = p2.replace(
                /(\r\n|\n|\r)/g,
                (match) => match + '    '
            );
            return (
                '"components": {\r\n        "schemas": {' +
                indentedContent +
                '}\r\n}'
            );
        }
    );

    const modifiedContent = stringWithReplacedDefinitions.replace(
        REF_DEFINITIONS_REGEX,
        '/components/schemas'
    );

    fs.writeFileSync(filePath, modifiedContent);
};
const main = () => {
    let path: string | null = null;
    process.argv.forEach((val, index) => {
        if (val === '--path' || val === '-p') {
            path = process.argv[index + 1];
        }
    });

    if (!path) {
        console.log(
            'Please provide a path to the schemas folder using --path or -p flag'
        );
        return;
    }
    const dir = fs.readdirSync(path);
    dir.forEach(
        (file) =>
            file.endsWith('.json') &&
            replaceDefinitionsWithComponents(`./schemas/${file}`)
    );
};

main();
