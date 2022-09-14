import seaborn as sns
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from constants import STEP


def plotTemporalObservable(simulationResultsDict, noiseObservableParameter):
    plt.rcParams.update({'font.size': 22})
    parameter = 'run'

    data = {
        parameter:[],
        'Time':[],
        'Fp':[]
    }

    step = STEP


    # for simulationResult in simulationResultsDict:
    step = 0
    print(simulationResultsDict.keys())
    for time, Fp in simulationResultsDict[(20,0.01)][0].fpDict.items():
        step += STEP
        data['Time'].append(step)
        data['Fp'].append(Fp)
        data[parameter].append(1)

    temporalObservableDataDF = pd.DataFrame(data)

    plt.figure(num="Temporal observable",figsize = (15,9))
    plt.title(f"Temporal observable: Polarization vs Time")
    plt.ylabel("Fp")
    plt.xlabel("Time (Steps)")
    ax = sns.lineplot(data=temporalObservableDataDF, x="Time", y="Fp",hue=parameter,legend="full",palette="pastel")
    sns.move_legend(ax,"lower right")
    plt.show()


def plotScalarObservable(simulationResults,noiseObservableParameter):
    plt.style.use('seaborn-pastel')
    plt.rcParams.update({'font.size': 22})

    simulationsByParameter = {}
    for simulationResult in simulationResults:
        dictionaryParameter = simulationResult.getDensity() if noiseObservableParameter else simulationResult.eta
        if dictionaryParameter not in simulationsByParameter:
            simulationsByParameter[dictionaryParameter] = [simulationResult]
        else: simulationsByParameter[dictionaryParameter].append(simulationResult)


    parameter = 'Noise' if noiseObservableParameter else 'Density'

    for dictParam, dictSimulationResults in simulationsByParameter.items():
        polarizationAverage = []
        parameterValues = []
        errors = []
        dictSimulationResults.sort(key=lambda x: x.eta if noiseObservableParameter else x.getDensity())
        

        for simulationResult in dictSimulationResults:
            ##Filtrar los Va desde el tiempo de estabilizacion
            estabilizedVaDict = dict(filter(lambda elem: elem[0] >= ESTABILIZATION_TIME ,simulationResult.vaDict.items()))

            parameterValue = simulationResult.eta if noiseObservableParameter else simulationResult.getDensity()
            parameterValues.append(parameterValue)
            polarizationValues = list(estabilizedVaDict.values())
            polarizationAverage.append(np.mean(polarizationValues))
            errors.append(np.std(polarizationValues))

            plt.figure(num="Scalar observable",figsize = (15,9))
            plt.title(f"Scalar observable: Polarization vs {parameter}")
            plt.ylabel("Polarization")
            plt.xlabel(parameter)

        plt.errorbar(parameterValues,polarizationAverage,yerr=errors,label=str(round(dictParam,2)))

    legendTitle = 'noises' if not noiseObservableParameter else 'densities'
    plt.legend(title=legendTitle)
    plt.show()



    