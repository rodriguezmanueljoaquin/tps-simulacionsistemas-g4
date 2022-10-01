from particle import Particle
import os
import copy


class SimulationResult:
    def __init__(self, method_name, mass, K, gamma, simulation_deltaT):
        self.particles_by_frame = list()
        self.method_name = method_name
        self.mass = mass
        self.K = K
        self.gamma = gamma
        self.simulation_deltaT = simulation_deltaT
        self.N = 1

class ParticlesFrame:
    def __init__(self):
        self.particles = list()
        self.time = 0

def read_input_files(input_files__directory_path):

    print('Reading input files. . .')

    simulations_results_dict = dict()

    dir_count = 0
    ##Iteramos en el path de los archivos de input
    for dir in os.listdir(input_files__directory_path):
        dir_count += 1
        print('\tReading directory '+str(dir_count)+'. . .')
        simulation_dir_path = input_files__directory_path +"/"+dir

        ##Por cada directorio, leemos los archivos estatico y dinamico correspondientes
        if(os.path.isdir(simulation_dir_path)):
            simulationResultsList = list()
            simulation_result_static = None
            for input_file in sorted(os.listdir(simulation_dir_path),reverse=True):
                ##Primero, leemos el archivo estatico
                if(input_file.lower().startswith('static')):
                    print('\t\tReading static file. . .')
                    simulation_result_static = __read_static_input_file(simulation_dir_path+"/"+input_file)
                    print('\t\tStatic file successfully read')
                else:
                    ##Luego, leemos el directorio de archivos dinamicos
                    print('\t\tReading dynamic files directory. . .')
                    dynamic_files_dir_path = simulation_dir_path+"/"+input_file
                    simulation_results_list = list()

                    if(os.path.isdir(dynamic_files_dir_path)):
                         for dynamicFilePath in os.listdir(dynamic_files_dir_path):
                            simulation_result_dynamic = copy.deepcopy(simulation_result_static)
                            print('\t\t\tReading dynamic file. . .')
                            __read_dynamic_input_file(dynamic_files_dir_path+"/"+dynamicFilePath,simulation_result_dynamic)
                            print('\t\t\tDynamic file successfully read')
                            simulation_results_list.append(simulation_result_dynamic)
                    print('\t\tDynamic files directory successfully read. . .')

                    simulations_results_dict[(simulation_result_static.simulation_deltaT,simulation_result_static.method_name)] = simulation_results_list
        print('\tDirectory successfully read. . .')

    print('Input files successfully read')

    return simulations_results_dict

def __read_static_input_file(static_input_file_path):
    lineCount = 0
    read = True
    
    file = open(static_input_file_path , 'r')

    line = file.readline()

    method_name = line.strip()
    line = file.readline()
    mass = float(line.strip())
    line = file.readline()
    K = float(line.strip())
    line = file.readline()
    gamma = float(line.strip())
    line = file.readline()
    simulation_deltaT = float(line.strip())
    line = file.readline()

    if line: raise Exception("Invalid static input file, there are more arguments than expected")
    file.close()

    return SimulationResult(method_name, mass, K, gamma, simulation_deltaT)

def __read_dynamic_input_file(dynamic_input_file_path, simulation_result):
    read = True
    
    file = open(dynamic_input_file_path , 'r')

    while read:
        line = file.readline()
        
        if not line:
            read = False
        else:
            current_time = float(line.strip())
            particles_frame = ParticlesFrame()
            particles_frame.time = current_time
            for _ in range(simulation_result.N):
                line = file.readline()
                particleData = line.split(";")
                x = float(particleData[0])
                vx = float(particleData[1])

                particles_frame.particles.append(Particle(0,x,0,vx,0))

            simulation_result.particles_by_frame.append(particles_frame)

    file.close()

    return simulation_result
