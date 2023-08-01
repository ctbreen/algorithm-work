import subprocess
import os

run_test_command = ['./binary'] # cpp
run_test_command = ['java', 'Main'] # java

input_path = './testcases/'
output_path = './testcases/expected_output/'

inputs = os.listdir(input_path)
for input_filename in inputs:
    if input_filename[-4:] != '.txt':
        continue
    print(input_filename)
    full_child_path = os.path.join(input_path, input_filename)
    

    with open(full_child_path, 'r') as input_file:
        input_contents = ''.join(input_file.readlines()).encode('utf-8')
    
    test_output = subprocess.check_output(run_test_command, input=input_contents, stderr=None).decode('utf-8')
    
    ans_path = os.path.join(output_path, input_filename.replace('testcase', 'output'))
    with open(ans_path, 'r') as ans_file:
        output_contents = ''.join(ans_file.readlines())
    if (test_output == output_contents):
        print('passed!')
    else:
        print(repr(test_output), repr(output_contents))
        exit(-1)