% Mark McKelvy
% CMPS 523
% Final Project
% File: plot_pose_error.m 

clear;
agent_data;
misc_data;
plot(time, pose_error)
title ('Robot pose error')
xlabel ('time (s)')
ylabel ('error (distance, cm)')
legend ('pose (positional) error')
