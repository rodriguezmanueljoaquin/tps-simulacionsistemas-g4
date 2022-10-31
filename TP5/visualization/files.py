from particle import Particle
import os
import copy


class SimulationResult:
    def __init__(self, humans_initial_qty, circle_radius, zombie_desired_velocity):
        self.particles_by_frame = list()
        self.humans_initial_qty = humans_initial_qty
        self.circle_radius = circle_radius
        self.zombie_desired_velocity = zombie_desired_velocity

    def __str__(self):
        return "SimulationResult: [humans_initial_qty={}, circle_radius={}, zombie_desired_velocity={}, particles_by_frame={}]".format(
            self.humans_initial_qty,
            self.circle_radius,
            self.zombie_desired_velocity,
            self.particles_by_frame
        )

    def __repr__(self):
        return self.__str__()

class ParticlesFrame:
    def __init__(self):
        self.particles = list()
        self.time = 0

def read_input_files(input_files__directory_path):

    print('Reading input files. . .')

    simulations_results = list()

    dir_count = 0
    ##Iteramos en el path de los archivos de input
    for dir in os.listdir(input_files__directory_path):
        dir_count += 1
        print('\tReading directory '+str(dir_count)+'. . .')
        simulation_dir_path = input_files__directory_path +"/"+dir

        ##Por cada directorio, leemos los archivos estatico y dinamico correspondientes
        if(os.path.isdir(simulation_dir_path)):
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
                            simulation_results_list.append(simulation_result_dynamic)
                            print('\t\t\tDynamic file successfully read')
                    print('\t\tDynamic files directory successfully read. . .')

                    simulations_results.append(simulation_results_list) 
        print('\tDirectory successfully read. . .')

    print('Input files successfully read')

    return simulations_results

def __read_static_input_file(static_input_file_path):
    file = open(static_input_file_path , 'r')
    line = file.readline()

    humans_initial_qty = int(line.strip())
    line = file.readline()
    circle_radius = float(line.strip())
    line = file.readline()
    zombie_desired_velocity = float(line.strip())
    line = file.readline()

    if line: raise Exception("Invalid static input file, there are more arguments than expected")
    file.close()

    return SimulationResult(humans_initial_qty, circle_radius, zombie_desired_velocity)
    
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
            ##Leemos las particulas
            for i in range(0, simulation_result.humans_initial_qty+1):
                line = file.readline()
                particles_frame.particles.append(__get_particle_data(line))

            simulation_result.particles_by_frame.append(particles_frame)

    file.close()
    return simulation_result

def __get_particle_data(line):
    data = line.split(";")
    id = int(data[0])
    x = float(data[1])
    y = float(data[2])
    vx = float(data[3])
    vy = float(data[4])
    radius = float(data[5])
    state = int(data[6])
    return Particle(id,x,y,vx,vy,radius,state)
