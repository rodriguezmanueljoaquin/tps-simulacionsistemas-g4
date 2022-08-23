import seaborn as sns
import pandas as pd
import matplotlib.pyplot as plt

def plotTemporalObservable(simulationResults):
    data = {
        'etas':[],
        'Tiempo':[],
        'Va':[]
    }

    for simulationResult in simulationResults:
        currentEta = simulationResult.eta
        for time,Va in simulationResult.vaDict.items():
            data['Tiempo'].append(time)
            data['Va'].append(Va)
            data['etas'].append(currentEta)

    temporalObservableDataDF = pd.DataFrame(data)

    plt.figure(figsize = (15,9))
    plt.title(f"Observable temporal: Va vs Tiempo")
    plt.ylabel("Va")
    sns.lineplot(data=temporalObservableDataDF, x="Tiempo", y="Va",hue="etas")
    plt.show()