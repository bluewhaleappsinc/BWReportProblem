package com.ultimuslab.imageeditengine.utils;

public interface TaskCallback<T> {
  void onTaskDone(T data);
}
