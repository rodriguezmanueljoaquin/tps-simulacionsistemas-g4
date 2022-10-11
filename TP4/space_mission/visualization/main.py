import argparse
import math
import exportOvito
from files import read_input_files
# from graphs import plot_particle_evolution_by_simulation,plot_error_graph


def read_results(input_files_directory_path):
    # input_files_directory_path ="../results"
    
    simulations_results_dict = read_input_files(input_files_directory_path)

    return simulations_results_dict

if __name__ == "__main__":
    input_files_directory_path ="../results"
    action = 'graph'
    argsValid = True
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument('-d','--InputFilesDirectory',dest='inputFilesDirectory')
        parser.add_argument('-a','--Action',dest='action')
        args = parser.parse_args()
        if(args.inputFilesDirectory is not None):
            input_files_directory_path = args.inputFilesDirectory
        if(args.action is not None):
            action = args.action.lower().strip()
            if(action != 'graph' and action != 'animate'):
                argsValid = False
    except Exception as e:
        print("Error in command line arguments")
        print(e)
    if(argsValid):

        ##Leemos los archivos de input
        simulations_results_dict = read_results(input_files_directory_path)

        # print(simulations_results_dict[(math.pow(10,-6),'GEAR')][0].particles_by_frame[3].particles)

        ##Luego, realizamos la accion correspondiente
        if(action=='animate'):
        # ANIMACION:
            simulation_results = list(simulations_results_dict.values())
            for simulation_result in simulation_results:
                exportOvito.exportOvito(simulation_result[0])

        else:
        # GRAFICACION:
            #TODO: Hacer las funciones de graficacion
            print('Graficos')

        # if(graph=='position'):
        #     # plot_particle_evolution_by_simulation(list(simulations_results_dict.values()))
        #     plot_particle_evolution_by_simulation(simulations_results_dict)
        # else:
        #     plot_error_graph(simulations_results_dict) 
    
    else:
        print("Invalid command line arguments")