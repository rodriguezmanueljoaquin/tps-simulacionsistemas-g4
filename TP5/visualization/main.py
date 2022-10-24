import argparse
import exportOvito
from files import read_input_files


if __name__ == "__main__":
    action = 'graph'
    argsValid = True
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument('-a','--Action',dest='action')
        args = parser.parse_args()
        if(args.action is not None):
            action = args.action.lower().strip()
            if(action != 'graph' and action != 'animate'):
                argsValid = False
    except Exception as e:
        print("Error in command line arguments")
        print(e)
    if(argsValid):

        ##Leemos los archivos de input
        simulations_results = read_input_files("../results/")

        ##Luego, realizamos la accion correspondiente
        if(action=='animate'):
        # ANIMACION:
            exportOvito.exportOvito(simulations_results[0][0])
        else:
        # GRAFICOS:
            pass

    else:
        print("Invalid command line arguments")
        