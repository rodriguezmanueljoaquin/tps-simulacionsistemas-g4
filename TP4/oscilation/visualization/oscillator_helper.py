import math

def get_analytic(time,A,K,gamma,mass):
    return A*math.exp((-1)*(gamma/(2*mass))*time)*math.cos(pow(((K/mass)-(pow(gamma,2)/(4*pow(mass,2)))),0.5)*time)

def get_cuadratic_error(simulation_result):
    N = simulation_result.N
    A = simulation_result.A
    K = simulation_result.K
    gamma = simulation_result.gamma
    mass = simulation_result.mass
    frames_count = len(simulation_result.particles_by_frame)
    error_sum = 0
    for particle_frame in simulation_result.particles_by_frame:
        time = particle_frame.time
        for particle in particle_frame.particles:
            error_sum += pow(particle.getPosition()[0]-get_analytic(time,A,K,gamma,mass),2)
        
    
    
    return error_sum/(N*frames_count)
