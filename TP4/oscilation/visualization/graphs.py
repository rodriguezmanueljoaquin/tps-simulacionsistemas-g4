import seaborn as sns
import pandas as pd
import matplotlib.pyplot as plt

def plot_curves_with_legend(curves, legends, X_label = "X", Y_label = "Y"):
    iters = range(1, len(curves[0]) + 1)
    colors = sns.color_palette("hls", len(legends))
    for i in range(len(curves)):
        plt.plot(iters, curves[i], label=legends[i], color=colors[i])

    plt.legend()
    plt.xlabel(X_label)
    plt.ylabel(Y_label)
    plt.show()

def plot_particle_evolution_by_simulation(simulation_results):
    curves = []
    legends = []
    for simulation_executions in simulation_results:
        # cada simulacion tiene multiples ejecuciones
        simulation_result = simulation_executions[0]
        position_evolution = []
        for frame in simulation_result.particles_by_frame:
            position_evolution.append(frame.particles[0].getPosition()[0])

        curves.append(position_evolution)
        legends.append(simulation_result.method_name + " deltaT= " + str(simulation_result.simulation_deltaT))

    plot_curves_with_legend(curves, legends, "Time", "Position")

