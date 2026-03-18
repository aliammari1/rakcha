import 'dart:convert';
import 'dart:math' as math;

import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:intl/intl.dart';
import 'package:timeago/timeago.dart' as timeago;
import 'lat_lng.dart';
import 'place.dart';
import 'uploaded_file.dart';
import '/backend/backend.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import '/auth/firebase_auth/auth_util.dart';

String joinList(
  List<String> list,
  String separator,
) {
  return list.join(separator);
}

double quantityPrice(
  double price,
  double quantity,
) {
  // multipled price and quantity
  return price * quantity;
}

double sumlist(List<double> price) {
  // add list of price
  double sum = 0.0;
  for (double p in price) {
    sum += p;
  }
  return sum;
}
