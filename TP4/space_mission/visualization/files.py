from particle import Particle
import os
import copy


class SimulationResult:
    def __init__(self, method_name, simulation_deltaT, sun_x, sun_y, sun_radius, earth_radius, venus_radius, spaceship_radius):
        self.particles_by_frame = list()
        self.method_name = method_name
        self.simulation_deltaT = simulation_deltaT
        self.sun_position = (sun_x,sun_y)
        self.sun_radius = sun_radius
        self.earth_radius = earth_radius
        self.venus_radius = venus_radius
        self.spaceship_radius = spaceship_radius

    def __str__(self):
        return "{method_name="+str(self.method_name)+";simulation_deltaT="+str(self.simulation_deltaT)+";sun_position="+str(self.sun_position)+";sun_radius="+str(self.sun_radius)+";earth_radius="+str(self.earth_radius)+";venus_radius="+str(self.venus_radius)+";spaceship_radius="+str(self.spaceship_radius)+"}"

    def __repr__(self):
        return self.__str__()

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
    simulation_deltaT = float(line.strip())
    line = file.readline()
    sun_data = line.split()[1].split(";")
    sun_x = float(sun_data[0])
    sun_y = float(sun_data[1])
    sun_radius = float(sun_data[2].strip())
    line = file.readline()
    earth_radius = float(line.split()[1].strip())
    line = file.readline()
    venus_radius = float(line.split()[1].strip())
    line = file.readline()
    spaceship_radius = float(line.split()[1].strip())     
    line = file.readline()    

    if line: raise Exception("Invalid static input file, there are more arguments than expected")
    file.close()

    return SimulationResult(method_name, simulation_deltaT, sun_x, sun_y, sun_radius, earth_radius, venus_radius, spaceship_radius)

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
            ##Leemos las posiciones de los planetas y de la nave
            #Tierra
            line = file.readline()
            earth = __get_space_particle(line,simulation_result.earth_radius)
            #Venus
            line = file.readline()
            venus = __get_space_particle(line, simulation_result.earth_radius)
            #Nave
            # line = file.readline()
            # spaceship = __get_space_particle(line, 2000)
            ##Las agregamos a la lista correspondiente en el orden: Tierra, Venus y nave
            particles_frame.particles.append(earth)
            particles_frame.particles.append(venus)
            #particles_frame.particles.append(spaceship)

            simulation_result.particles_by_frame.append(particles_frame)

    file.close()

    return simulation_result

def __get_space_particle(line, radius):
    id = line.split()[0]
    data = line.split()[1].strip().split(";")
    x = float(data[0])
    y = float(data[1])
    vx = float(data[2])
    vy = float(data[3].strip())
    return Particle(id,x,y,vx,vy,radius)
