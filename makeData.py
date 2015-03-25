with open("create.txt") as f:
    content = f.readlines()
for x in content:
    print x

synth = open("synthData.txt", 'w')
for x in range(0, 1000, 1):
    for x in content:
        synth.write(x) 
    for x in reversed(content):
        synth.write(x)
