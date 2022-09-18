from math import ceil, floor
import seaborn as sns
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from constants import MAX_STEP, STEP, PARAM_GAP_SIZE, PARAM_PARTICLES_QTY, UMBRAL
from files import removeItemsOutOfStepAndMaxStep
from scalarObservableObject import ScalarObservableObject


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

    simulationsResultsList = list(simulationResultsDict.items())
    simulationsResultsList.sort(key=lambda x: x[0][param_key])
    for simParams, simulationsResults in simulationsResultsList:
        # simParams es de la forma (N, abertura)
        simulationResult = simulationsResults[0] 
        # solo nos interesa el primero, se podria tomar el promedio de todas las ejecuciones pero es innecesario y posiblemente no se visualize bien
        removeItemsOutOfStepAndMaxStep(simulationResult)
        for time, Fp in simulationResult.fpDict.items():
            data['Time'].append(ceil(time/STEP)*STEP)
            data['Fraction'].append(Fp)
            data[parameter].append(str(simParams[param_key]) + (" m" if observableParameter == PARAM_GAP_SIZE else ""))

    temporalObservableDataDF = pd.DataFrame(data)

    plt.figure(num="Temporal observable",figsize = (15,9))
    #plt.title(f"Temporal observable: Fracción de partículas vs {parameter}")
    plt.ylabel("Fracción de partículas")
    plt.xlabel("Tiempo (Pasos)")
    ax = sns.lineplot(data=temporalObservableDataDF, x="Time", y="Fraction",
                hue=parameter, legend="full",palette="pastel")
    ax.axhline(y=0.5+UMBRAL, color='r', linestyle='--', label="Umbral ("+str(UMBRAL)+")")
    ax.axhline(y=0.5-UMBRAL, color='r', linestyle='--')
    ax.legend(title=parameter)
    sns.move_legend(ax,"upper right")
    plt.show()


def plotScalarObservable(simulationResultsDict, observableParameter):
    # Las key del diccionario son de la forma (N, gap)
    if observableParameter == PARAM_PARTICLES_QTY:
        param_key =0
    elif observableParameter == PARAM_GAP_SIZE:
        param_key =1
    else: raise Exception("Invalid observable parameter")

    plt.style.use('seaborn-pastel')
    plt.rcParams.update({'font.size': 22})

    simulationsByParameter = {}
    for simParams, simulationsResults in simulationResultsDict.items():
        dictionaryParameter = simParams[1-param_key]
        if dictionaryParameter not in simulationsByParameter:
            simulationsByParameter[dictionaryParameter] = [ScalarObservableObject(simParams[param_key],simulationsResults)]
        else: simulationsByParameter[dictionaryParameter].append(ScalarObservableObject(simParams[param_key],simulationsResults))

    parameter = "Cantidad de partículas" if observableParameter == PARAM_PARTICLES_QTY else "Tamaño de abertura"

    plt.figure(num="Scalar observable",figsize = (15,9))
    plt.ylabel("Tiempo de equilibrio")
    plt.xlabel(parameter)

    for dictParam, scalarObservableObjectList in simulationsByParameter.items():
        balanceTimeAverage = []
        parameterValues = []
        errors = []
        scalarObservableObjectList .sort(key=lambda x: x.param)
        

        for scalarObservableObject in scalarObservableObjectList:
            parameterValues.append(scalarObservableObject.param)
            balanceTimeList = [simulationResult.balanceTime for simulationResult in scalarObservableObject.simulationResults]
            balanceTimeAverage.append(np.mean(balanceTimeList))
            errors.append(np.std(balanceTimeList))

        plt.errorbar(parameterValues,balanceTimeAverage,yerr=errors,label=str(round(dictParam,2))+(" m" if observableParameter == PARAM_GAP_SIZE else ""))

    legendTitle = "Cantidad de partículas" if observableParameter == PARAM_GAP_SIZE else "Tamaño de abertura"
    plt.legend(title=legendTitle)
    plt.show()




    