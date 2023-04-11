import os
import json

path_of_the_directory = './schemas/'

for filename in os.listdir(path_of_the_directory):
    f_path = os.path.join(path_of_the_directory, filename)
    if os.path.isfile(f_path):
        f = open(f_path, 'r', encoding="utf8")
        json_object = json.load(f)
        f.close

        json_object_as_string = str(json_object)
        json_object_as_string = json_object_as_string.replace('./schemas/', '')

        if json_object_as_string == str(json_object):
            continue

        data = eval(json_object_as_string)

        # Re-open file here
        f2 = open(f_path, 'w', encoding="utf8")
        json.dump(data, f2, indent=4)
        # for line in lineList:
        #     line = line.replace('./schemas/', '')
        #     f2.write(line)
        f2.close()

os.system("java -jar swagger-codegen-cli-3.0.36.jar generate -i openapi.json -l python -o unit")

path_of_the_directory = './unit/swagger_client/models/'

for filename in os.listdir(path_of_the_directory):
    f_path = os.path.join(path_of_the_directory, filename)
    if os.path.isfile(f_path):
        f = open(f_path, 'r')
        lineList = f.readlines()
        f.close

        # Re-open file here
        f2 = open(f_path, 'w')
        for line in lineList:
            line = line.replace('#/components/schemas/', '')
            f2.write(line)
        f2.close()

f_path = "unit/swagger_client/api/create_application_api.py"
f = open(f_path, 'r')
lineList = f.readlines()
f.close

# Re-open file here
f2 = open(f_path, 'w')
for line in lineList:
    line = line.replace('_create_application', 'create_application')
    f2.write(line)
f2.close()



