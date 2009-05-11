% Mark McKelvy
% CMPS 523
% Final Project
% File: plot_poses.m

clear;
misc_data;
agent_data;
testbed_data;
plot(agent_poses(1,:), agent_poses(2,:), 'r', testbed_poses(1,:), testbed_poses(2,:), 'b', objects_loc_x, objects_loc_y, '.')
axis([0 1600 0 1600])
title ('Robot actual (blue) vs. believed position (red)')
xlabel ('x')
ylabel ('y')
