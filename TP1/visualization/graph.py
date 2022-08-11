import matplotlib.pyplot as plt


def makeParticlesGraph(particlesDict,neighboursDict,graphPropertiesDict,particleId):
    L = graphPropertiesDict.get('L')
    fig = plt.figure()
    ax = plt.axes(xlim=(0, L), ylim=(0, L), aspect=True)
    
    principalNeighbours = neighboursDict.get(particleId)


    for id,particle in particlesDict.items():
        x = particle.x
        y = particle.y
        radius = particle.radius
        particleColor = __getParticleColor(id,particleId,principalNeighbours)
        circle = plt.Circle((x,y),radius,facecolor=particleColor)
        ax.add_patch(circle)
        ax.annotate(id,(x,y))

    plt.show()


def __getParticleColor(currentParticleId,principalParticleId,principalParticleNeighbours):
    
    if(currentParticleId==principalParticleId):
        return 'g'
    if(currentParticleId in principalParticleNeighbours):
        return 'r'
    return 'b'