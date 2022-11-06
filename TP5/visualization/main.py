import argparse
import exportOvito
from files import read_input_files
from graphs import plot_scalar_observable,plot_temporal_observable,plot_coefficient

if __name__ == "__main__":
    #Valores default de los argumentos
    action = 'graph'
    input_files_directory_path = "../results"
    observable_type = 'temporal'
    observable = 'zombie_fraction'
    observable_variable = 'humans_initial_qty'
    args_valid = True
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument('-a','--Action',dest='action')
        parser.add_argument('-d','--InputFilesDirectory',dest='input_files_directory')
        parser.add_argument('-v','--Variable',dest='variable')
        parser.add_argument('-o','--Observable',dest='observable')
        parser.add_argument('-t','--ObservableType',dest='observable_type')
        args = parser.parse_args()
        #Parseamos los distintos argumentos
        ##action
        if(args.action is not None):
            action = args.action.lower().strip()
            if(action != 'graph' and action != 'animate' and action != 'coeff_evol'):
                argsValid = False
        ##input_files_directory
        if(args.input_files_directory is not None):
            input_files_directory_path = args.input_files_directory
        ##variable
        if(args.variable is not None):
            observable_variable = args.variable.lower().strip()
            if((observable_variable!='humans_initial_qty' and observable_variable!='zombie_desired_velocity' and observable_variable !="square")):
                argsValid=False
        ##observable_type
        if(args.observable_type is not None):
            observable_type = args.observable_type.lower().strip()
            if((observable_type!='scalar' and observable_type!='temporal' ) or action=='animate'):
                args_valid = False
        ##observable
        if(args.observable is not None):
            observable = args.observable.lower().strip()
            if((observable!='zombie_fraction' and observable!='contagion_speed') or action=='animate'):
                args_valid = False
        
    except Exception as e:
        print("Error in command line arguments")
        print(e)
    if(args_valid):
        ##Leemos los archivos de input
        simulations_results = read_input_files(input_files_directory_path)

        ##Luego, realizamos la accion correspondiente
        if(action=='animate'):
        # ANIMACION:
            exportOvito.exportOvito(simulations_results[0][0], observable_variable)
        elif(action=='coeff_evol'):
            plot_coefficient(simulations_results)
        else:
        # GRAFICOS:
            #Primero, chequeamos el tipo de observable a realizar (escalar o temporal) y luego cual se quiere realizar (fraccion de zombies o velocidad de contagio)
            if(observable_type=='scalar'):
                plot_scalar_observable(simulations_results,observable_variable,observable)
            else:
                plot_temporal_observable(simulations_results,observable_variable,observable)

    else:
        print("Invalid command line arguments")
        