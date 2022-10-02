import argparse
from files import read_input_files
from graphs import plot_particle_evolution_by_simulation,plot_error_graph


def read_results(input_files_directory_path):
    # input_files_directory_path ="../results"
    
    simulations_results_dict = read_input_files(input_files_directory_path)

    return simulations_results_dict

if __name__ == "__main__":
    input_files_directory_path ="../results"
    graph = "position"
    argsValid = True
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument('-d','--InputFilesDirectory',dest='inputFilesDirectory')
        parser.add_argument('-g','--Graph',dest='graph')
        args = parser.parse_args()
        if(args.inputFilesDirectory is not None):
            input_files_directory_path = args.inputFilesDirectory
        if(args.graph is not None):
            graph = args.graph.lower().strip()
            if(graph != 'position' and graph != 'error'):
                argsValid = False
    except Exception as e:
        print("Error in command line arguments")
        print(e)
    if(argsValid):

        ##Leemos los archivos de input
        simulations_results_dict = read_results(input_files_directory_path)

        # print(simulations_results_dict)

        ##Luego, hacemos el grafico correspondiente
        if(graph=='position'):
            # plot_particle_evolution_by_simulation(list(simulations_results_dict.values()))
            plot_particle_evolution_by_simulation(simulations_results_dict)
        else:
            plot_error_graph(simulations_results_dict) 
    
    else:
        print("Invalid command line arguments")


    