import fs from 'fs';

const REF_DEFINITIONS_REGEX = /(^.*"\$ref":\s*".*)(definitions)/gm;
const DEFINITIONS_REGEX = /("definitions":\s*{)((.|\n|\r)*)(})/;

const replaceDefinitionsWithComponents = (filePath: string) => {
    const fileContents = fs.readFileSync(filePath, { encoding: 'utf-8' });

    const stringWithReplacedDefinitions = fileContents.replace(
        DEFINITIONS_REGEX,
        (match, p1, p2: string, p3) => {
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
        (match, p1) => {
            return p1 + 'components/schemas';
        }
    );

    fs.writeFileSync(filePath, modifiedContent);
};

const dir = fs.readdirSync('./schemas');
dir.forEach(
    (file) =>
        file.endsWith('.json') &&
        replaceDefinitionsWithComponents(`./schemas/${file}`)
);
