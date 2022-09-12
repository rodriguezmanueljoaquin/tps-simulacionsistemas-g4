

import ovito
import ovito.modifiers 


def particle_modifier(frame, data):
    data.cell.vis.enabled = False
    data.particles.display.shape = ovito.vis.ParticlesVis.Shape.Circle
    data.particles.display.radius=0.1

def animation():
    particles = ovito.io.import_file("particles.xyz", columns=["Position.X", "Position.Y", "Displacement.X","Displacement.Y", "Color.R","Color.G","Color.B"])
    particles.modifiers.append(particle_modifier)
    displacement = ovito.modifiers.CalculateDisplacementsModifier()
    particles.modifiers.append(displacement)
    displacement.vis.enabled = True       # Enable the display of arrows
    displacement.vis.color = (0,1,0)
    displacement.vis.width = 0.01
    displacement.vis.Alignment.Base 
    particles.add_to_scene()

    vp = ovito.vis.Viewport()
    vp.type = ovito.vis.Viewport.Type.TOP
    vp.fov = 6
    vp.camera_pos = (0,0,0)
    vp.render_anim(size=(1200, 800),filename="lattice.mp4", fps=20, background=(0, 0, 0), every_nth=1)
    particles.remove_from_scene()
