part of 'increment_bloc.dart';

abstract class CounterEvent {}


class Increment extends CounterEvent {}

class Decrement extends CounterEvent {}

class Reset extends CounterEvent {}