import matplotlib.pyplot as plt

NEIGHBOURHOOD_RADIUS = 0.5 # DEBERIA LLEGAR POR PARAMETRO?

def makeParticlesGraph(particlesDict,neighboursDict,graphPropertiesDict,particleId):
    L = graphPropertiesDict.get('L')
    fig = plt.figure()
    ax = plt.axes(xlim=(0, L), ylim=(0, L), aspect=True)
    
    principalNeighbours = neighboursDict.get(particleId)


    for id,particle in particlesDict.items():
        particleColor = __getParticleColor(id,particleId,principalNeighbours)
        circle = plt.Circle(particle.getPosition(),particle.radius,facecolor=particleColor)
        ax.add_patch(circle)
        ax.annotate(id, particle.getPosition())
        if(id == particleId):
            ax.add_patch(plt.Circle(particle.getPosition(),
                             radius=particle.radius + graphPropertiesDict.get('RC'),
                             color='k', linewidth=1, fill=False))
    plt.show()


def __getParticleColor(currentParticleId,principalParticleId,principalParticleNeighbours):
    
    if(currentParticleId==principalParticleId):
        return 'g'
    if(currentParticleId in principalParticleNeighbours):
        return 'r'
    return 'b'