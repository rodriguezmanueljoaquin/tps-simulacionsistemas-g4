import argparse
import exportOvito
from files import read_input_files
from graphs import plot_trip_distance_evolution, plot_minimum_distance_by_start_simulation_date, plot_minimum_time_by_initial_velocity_module, plot_velocity_evolution


def read_results(input_files_directory_path):
    simulations_results_dict = read_input_files(input_files_directory_path)

    return simulations_results_dict

if __name__ == "__main__":
    input_files_directory_path ="../results/earth_to_venus"
    action = 'graph'
    variable = 'seconds_to_departure'
    argsValid = True
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument('-d','--InputFilesDirectory',dest='inputFilesDirectory')
        parser.add_argument('-a','--Action',dest='action')
        parser.add_argument('-v','--Variable',dest='variable')
        args = parser.parse_args()
        if(args.inputFilesDirectory is not None):
            input_files_directory_path = args.inputFilesDirectory
        if(args.action is not None):
            action = args.action.lower().strip()
            if(action != 'graph' and action != 'animate'):
                argsValid = False
        if(args.variable is not None):
            variable = args.variable.lower().strip()
            if(variable != 'seconds_to_departure' and variable != 'initial_velocity_module' 
                    and variable != 'distance_evolution' and variable != 'velocity_evolution'):
                argsValid = False
    except Exception as e:
        print("Error in command line arguments")
        print(e)
    if(argsValid):

        ##Leemos los archivos de input
        simulations_results = read_results(input_files_directory_path)

        # print(simulations_results_dict[(math.pow(10,-6),'GEAR')][0].particles_by_frame[3].particles)

        ##Luego, realizamos la accion correspondiente
        if(action=='animate'):
        # ANIMACION:
            exportOvito.exportOvito(simulations_results[0])

            for simulation_result in simulations_results:
                if simulation_result.seconds_to_departure == 60*60*24*10*23:
                    exportOvito.exportOvito(simulation_result)

        else:
        # GRAFICOS:
            if variable == 'seconds_to_departure':
                plot_minimum_distance_by_start_simulation_date(simulations_results)
            elif variable == 'initial_velocity_module':
                plot_minimum_time_by_initial_velocity_module(simulations_results)
            elif variable == 'velocity_evolution':
                plot_velocity_evolution(simulations_results[0])
            elif variable == 'distance_evolution':
                plot_trip_distance_evolution(simulations_results[0])

    else:
        print("Invalid command line arguments")
        