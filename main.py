# coding=utf-8
import requests
# from urlparse import urlparse
from urllib.parse import urlparse
import core
from generator import generator
import SQLInjection
import parser

def parse():
    parser.add_option('-i', type="string", help='The path of SQL Parser package ',
                      dest='input_path')
    base = core.Core()
    # base.banner()
    base.cmd_option()
    result = base.parse_log(core.burp_suite_log)
    for item in result:
        if base.gerequestinfo(item, "Host") == core.target_domain:
            headers = {}
            sp = item.split('\n\n', 1)
            if len(sp) > 1:
                head = sp[0]
                body = sp[1]
            else:
                head = sp[0]
                body = ""
            c1 = head.split('\n', head.count('\n'))
            method = c1[0].split(' ', 2)[0]
            path = c1[0].split(' ', 2)[1]
            for i in range(1, head.count('\n') + 1):
                slice1 = c1[i].split(': ', 1)
                if slice1[0] != "":
                    headers[slice1[0]] = slice1[1]

            session = requests.session()
            if method == 'GET':
                url = base.gerequestinfo(item, "Host") + path
                domain = url.split("?")[0]
                queries = urlparse(url).query.split("&")
                if not any(queries):
                    print ('skip')
                else:
                    for i in range(len(queries)):
                        input = queries[i].split('=')[1]
                        output, payload = generator(input)
                        queries[i] = queries[i].split('=')[0]+'='+payload
                    website = domain + "?" + ("&".join([param for param in queries]))
                    print (website)
                    session.get(url=website)
                    with open(input_path, 'r') as f:
                        sqltext = f.readline()
                    if SQLInjection.isInjected(sqltext):
                        with open('./output.txt', 'a') as f:
                            f.write(website+'\n')
            elif method == 'POST':
                print (body)
                queries = urlparse(body).query.split("&")
                if not any(queries):
                    print ('skip')
                else:
                    for i in range(len(queries)):
                        input = queries[i].split('=')[1]
                        output = generator(input)
                        queries[i] = queries[i].split('=')[0]+'='+output
                    website = domain + "?" + ("&".join([param for param in queries]))
                    print (website)
                    session.get(url=website)
                    with open(input_path, 'r') as f:
                        sqltext = f.readline()
                    if SQLInjection.isInjected(sqltext):
                        with open('./output.txt', 'a') as f:
                            f.write(website + '\n')


