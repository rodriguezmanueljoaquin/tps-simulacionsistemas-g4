

class ScalarObservableObject:

    def __init__(self,param,simulationResults):
        self.param = param
        self.simulationResults = simulationResults

    def __str__(self):
        return "{param="+str(self.param)+";simulationResults="+str(self.simulationResults)+"}"

    def __repr__(self):
        return self.__str__()
