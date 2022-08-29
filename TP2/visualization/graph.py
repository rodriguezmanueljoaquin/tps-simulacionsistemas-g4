import seaborn as sns
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from constants import ESTABILIZATION_TIME

observablesPath = "./observables"

def plotObservables(simulationResults,noiseObservableParameter):
    plt.rcParams.update({'font.size': 22})
    ##Armamos el observable temporal
    __plotTemporalObservable(simulationResults,noiseObservableParameter)
    ##Armamos el observable escalar
    __plotScalarObservable(simulationResults,noiseObservableParameter)
    ##Graficamos ambos observables
    plt.show()


def __plotTemporalObservable(simulationResults,noiseObservableParameter):
    
    parameter = 'noises'

    if(not noiseObservableParameter):
        parameter = 'densities'
    
    data = {
        parameter:[],
        'Time':[],
        'Polarization':[]
    }

    for simulationResult in simulationResults:
        paramValue = simulationResult.eta
        if(not noiseObservableParameter):
            paramValue = simulationResult.getDensity()
        for time,Va in simulationResult.vaDict.items():
            data['Time'].append(time)
            data['Polarization'].append(Va)
            data[parameter].append(paramValue)

    temporalObservableDataDF = pd.DataFrame(data)

    plt.figure(num="Temporal observable",figsize = (15,9))
    plt.title(f"Temporal observable: Polarization vs Time")
    plt.ylabel("Polarization")
    plt.xlabel("Time (Steps)")
    sns.lineplot(data=temporalObservableDataDF, x="Time", y="Polarization",hue=parameter,legend="full",palette="pastel")


def __plotScalarObservable(simulationResults,noiseObservableParameter):

    parameter = 'Noise'

    if(not noiseObservableParameter):
        parameter = 'Density'


    polarizationAverage = []
    parameterValues = []
    errors = []
    

    for simulationResult in simulationResults:
        ##Filtrar los Va desde el tiempo de estabilizacion
        estabilizedVaDict = dict(filter(lambda elem: elem[0] >= ESTABILIZATION_TIME ,simulationResult.vaDict.items()))

        parameterValue = simulationResult.eta
        if(not noiseObservableParameter):
            parameterValue = simulationResult.getDensity()
        parameterValues.append(parameterValue)
        polarizationValues = list(estabilizedVaDict.values())
        polarizationAverage.append(np.mean(polarizationValues))
        errors.append(np.std(polarizationValues))

        plt.figure(num="Scalar observable",figsize = (15,9))
        plt.title(f"Scalar observable: Polarization vs {parameter}")
        plt.ylabel("Polarization")
        plt.xlabel(parameter)

    plt.errorbar(parameterValues,polarizationAverage,yerr=errors)



    