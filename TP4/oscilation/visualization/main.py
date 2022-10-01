from files import read_input_files
from graphs import plot_particle_evolution_by_simulation


def read_results():
    input_files_directory_path ="../results"
    
    simulations_results_dict = read_input_files(input_files_directory_path)

    return simulations_results_dict

if __name__ == "__main__":
    simulations_results_dict = read_results()
    print(simulations_results_dict)
    plot_particle_evolution_by_simulation(list(simulations_results_dict.values()))


    