import seaborn as sns
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from constants import STEP, PARAM_GAP_SIZE, PARAM_PARTICLES_QTY
from files import removeItemsOutOfStepAndMaxStep


def plotTemporalObservable(simulationResultsDict, observableParameter):
    # Las key del diccionario son de la forma (N, gap)
    if observableParameter == PARAM_PARTICLES_QTY:
        param_key =0
    elif observableParameter == PARAM_GAP_SIZE:
        param_key =1
    else: raise Exception("Invalid observable parameter")

    plt.rcParams.update({'font.size': 22})
    parameter = "Cantidad de partículas" if observableParameter == 'particles_number' else "Tamaño de abertura"

    data = {
        parameter:[],
        'Time':[],
        'Fraction':[]
    }

    for simParams, simulationsResults in simulationResultsDict.items():
        # simParams es de la forma (N, abertura)
        simulationResult = simulationsResults[0] # solo nos interesa el primero, se podria tomar el promedio de todas las ejecuciones pero es innecesario y posiblemente no se visualize bien
        step = 0
        removeItemsOutOfStepAndMaxStep(simulationResult)
        for time, Fp in simulationResult.fpDict.items():
            step += STEP
            data['Time'].append(step)
            data['Fraction'].append(Fp)
            data[parameter + (" m" if observableParameter == PARAM_GAP_SIZE else "")].append(simParams[param_key])

    temporalObservableDataDF = pd.DataFrame(data)

    plt.figure(num="Temporal observable",figsize = (15,9))
    plt.title(f"Temporal observable: Fracción de partículas vs {parameter}")
    plt.ylabel("Fracción de partículas")
    plt.xlabel("Tiempo (Pasos)")
    ax = sns.lineplot(data=temporalObservableDataDF, x="Time", y="Fraction",
                hue=parameter, legend="full",palette="pastel")
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



    