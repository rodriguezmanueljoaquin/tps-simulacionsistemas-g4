import matplotlib.pyplot as plt


def makeParticlesGraph(particlesDict,neighboursDict,graphPropertiesDict,particleId):
    L = graphPropertiesDict.get('L')
    plt.figure(figsize=(L, L))
    
    principalNeighbours = neighboursDict.get(particleId)


    for id,particle in particlesDict.items():
        x = particle.x
        y = particle.y
        particleColor = __getParticleColor(id,particleId,principalNeighbours)
        plt.scatter(x,y,color=particleColor)
        plt.annotate(id,(x,y))

    plt.show()


def __getParticleColor(currentParticleId,principalParticleId,principalParticleNeighbours):
    
    if(currentParticleId==principalParticleId):
        return 'green'
    if(currentParticleId in principalParticleNeighbours):
        return 'red'
    return 'blue'