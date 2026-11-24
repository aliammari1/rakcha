import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

import 'core.dart';

List<CinemaData> _owned(List<CinemaData> cinemas) {
  final uid = FirebaseAuth.instance.currentUser?.uid;
  return cinemas.where((cinema) => cinema.managerId == uid).toList();
}

Widget _signOut() => IconButton(
      tooltip: 'Sign out',
      icon: const Icon(Icons.logout),
      onPressed: () => FirebaseAuth.instance.signOut(),
    );
// ---------------------------------------------------------------------------
// Cinema manager
// ---------------------------------------------------------------------------

class ManagerHome extends StatefulWidget {
  const ManagerHome({super.key});

  @override
  State<ManagerHome> createState() => _ManagerHomeState();
}

class _ManagerHomeState extends State<ManagerHome> {
  int _tab = 0;

  @override
  Widget build(BuildContext context) => Scaffold(
        body: IndexedStack(index: _tab, children: const [
          ManagerOverview(),
          ShowtimesTab(),
          ManagerCinemasTab(),
        ]),
        bottomNavigationBar: NavigationBar(
          selectedIndex: _tab,
          onDestinationSelected: (i) => setState(() => _tab = i),
          backgroundColor: surface,
          indicatorColor: red,
          destinations: const [
            NavigationDestination(
                icon: Icon(Icons.space_dashboard_outlined),
                selectedIcon: Icon(Icons.space_dashboard),
                label: 'Overview'),
            NavigationDestination(
                icon: Icon(Icons.event_outlined),
                selectedIcon: Icon(Icons.event),
                label: 'Showtimes'),
            NavigationDestination(
                icon: Icon(Icons.theaters_outlined),
                selectedIcon: Icon(Icons.theaters),
                label: 'Cinemas'),
          ],
        ),
      );
}

class ManagerOverview extends StatelessWidget {
  const ManagerOverview();

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
          actions: [_signOut()],
          title:
              Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            kText('RAKCHA WORKSPACE', size: 11, weight: FontWeight.w900),
            kText('Cinema management', size: 13, color: muted),
          ]),
        ),
        body: StreamBuilder(
          stream: watchCinemas(),
          builder: (_, cSnap) => StreamBuilder(
            stream: watchShowtimes(),
            builder: (_, sSnap) {
              final cinemas = _owned(cSnap.data ?? []);
              final shows = (sSnap.data ?? [])
                  .where(
                    (show) =>
                        cinemas.any((cinema) => cinema.id == show.cinemaId),
                  )
                  .toList()
                ..sort((a, b) =>
                    '${a.date} ${a.hd}'.compareTo('${b.date} ${b.hd}'));
              return SingleChildScrollView(
                padding: const EdgeInsets.all(20),
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      kEyebrow('Cinema management'),
                      kText('YOUR CINEMAS', size: 26, weight: FontWeight.w900),
                      const SizedBox(height: 8),
                      kText(
                          'Manage the venues you look after, their rooms and the films you schedule.',
                          size: 12,
                          color: muted),
                      const SizedBox(height: 20),
                      Row(children: [
                        Expanded(
                            child: kKpi('${cinemas.length}', 'Cinemas',
                                Icons.theaters)),
                        const SizedBox(width: 8),
                        Expanded(
                            child: kKpi(
                                '${shows.length}', 'Showtimes', Icons.event)),
                        const SizedBox(width: 8),
                        Expanded(
                            child: kKpi(
                                '${shows.map((show) => show.filmId).toSet().length}',
                                'Films scheduled',
                                Icons.movie)),
                      ]),
                      const SizedBox(height: 26),
                      kSectionHead('Upcoming', 'Next showtimes'),
                      const SizedBox(height: 12),
                      ...shows.take(4).map((s) => Card(
                            child: ListTile(
                              leading: const Icon(Icons.event_available,
                                  color: redHot),
                              title: kText('${s.date} · ${s.hd}',
                                  size: 13, weight: FontWeight.w700),
                              subtitle: kText(
                                  '${s.room} · ${s.price.toStringAsFixed(0)} TND',
                                  size: 11,
                                  color: muted),
                            ),
                          )),
                    ]),
              );
            },
          ),
        ),
      );
}

class ShowtimesTab extends StatefulWidget {
  const ShowtimesTab();
  @override
  State<ShowtimesTab> createState() => ShowtimesTabState();
}

class ShowtimesTabState extends State<ShowtimesTab> {
  final _cinemas = watchCinemas();
  final _films = watchFilms();
  final _shows = watchShowtimes();

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
            title: kText('Showtimes', size: 22, weight: FontWeight.w900)),
        body: StreamBuilder(
          stream: _cinemas,
          builder: (_, venues) => StreamBuilder(
            stream: _films,
            builder: (_, catalogue) => StreamBuilder(
              stream: _shows,
              builder: (context, programme) {
                if (venues.hasError ||
                    catalogue.hasError ||
                    programme.hasError) {
                  return const Center(
                      child: Text('Could not load the programme.'));
                }
                if (!venues.hasData ||
                    !catalogue.hasData ||
                    !programme.hasData) {
                  return const Center(child: CircularProgressIndicator());
                }
                final cinemas = {
                  for (final cinema in _owned(venues.data!)) cinema.id: cinema
                };
                final films = {
                  for (final film in catalogue.data!) film.id: film
                };
                final shows = programme.data!
                    .where((show) => cinemas.containsKey(show.cinemaId))
                    .toList()
                  ..sort((a, b) =>
                      '${b.date} ${b.hd}'.compareTo('${a.date} ${a.hd}'));
                final open = cinemas.values
                    .where((cinema) => cinema.status == 'Accepted')
                    .toList();
                return ListView(padding: const EdgeInsets.all(16), children: [
                  kSectionHead('Programming', '${shows.length} screenings'),
                  const SizedBox(height: 12),
                  kButton(
                      'Schedule a film',
                      open.isEmpty || films.isEmpty
                          ? null
                          : () => _openForm(open, films.values.toList()),
                      icon: Icons.add),
                  const SizedBox(height: 14),
                  for (final show in shows)
                    Card(
                        child: ListTile(
                      leading: const Icon(Icons.movie_outlined, color: redHot),
                      title: kText(films[show.filmId]?.name ?? 'Film screening',
                          size: 14, weight: FontWeight.w800),
                      subtitle: kText(
                          '${cinemas[show.cinemaId]!.name} · ${show.room}\n${show.date} · ${show.hd}',
                          size: 12,
                          color: muted),
                      trailing: kText('${show.price.toStringAsFixed(2)} TND',
                          size: 11, color: gold),
                    )),
                ]);
              },
            ),
          ),
        ),
      );

  Future<void> _openForm(List<CinemaData> cinemas, List<FilmData> films) async {
    final saved = await showRakchaSheet<bool>(
      context: context,
      builder: (_) => ScheduleSheet(cinemas: cinemas, films: films),
    );
    if (mounted && saved == true)
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Showtime scheduled')),
      );
  }
}

class ScheduleSheet extends StatefulWidget {
  const ScheduleSheet({required this.cinemas, required this.films});
  final List<CinemaData> cinemas;
  final List<FilmData> films;

  @override
  State<ScheduleSheet> createState() => ScheduleSheetState();
}

class ScheduleSheetState extends State<ScheduleSheet> {
  final room = TextEditingController(text: 'Room 1');
  final price = TextEditingController();
  String? cinema, film;
  DateTime date =
      DateUtils.dateOnly(DateTime.now()).add(const Duration(days: 1));
  TimeOfDay start = const TimeOfDay(hour: 19, minute: 0);
  TimeOfDay end = const TimeOfDay(hour: 21, minute: 30);
  bool saving = false;
  String? error;

  String get dateValue => date.toIso8601String().split('T').first;
  String _timeValue(TimeOfDay value) =>
      '${value.hour.toString().padLeft(2, '0')}:${value.minute.toString().padLeft(2, '0')}';

  @override
  void dispose() {
    room.dispose();
    price.dispose();
    super.dispose();
  }

  Future<void> _save() async {
    final amount = double.tryParse(price.text.replaceAll(',', '.'));
    if (cinema == null ||
        film == null ||
        room.text.trim().isEmpty ||
        amount == null ||
        !amount.isFinite ||
        amount <= 0 ||
        _timeValue(end).compareTo(_timeValue(start)) <= 0) {
      setState(() => error =
          'Choose a cinema and film, and check the room, times and price.');
      return;
    }
    setState(() {
      saving = true;
      error = null;
    });
    try {
      await createShowtime(
          cinemaId: cinema!,
          filmId: film!,
          room: room.text.trim(),
          hd: _timeValue(start),
          hf: _timeValue(end),
          date: dateValue,
          price: amount);
      if (mounted) Navigator.pop(context, true);
    } catch (_) {
      if (mounted)
        setState(() {
          saving = false;
          error = 'Could not schedule the film. Try again.';
        });
    }
  }

  @override
  Widget build(BuildContext context) => SizedBox(
        width: double.infinity,
        child: Padding(
          padding: EdgeInsets.only(
              bottom: MediaQuery.of(context).viewInsets.bottom,
              left: 20,
              right: 20),
          child: SingleChildScrollView(
              child: Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                kEyebrow('Programming'),
                kText('Schedule a film', size: 22, weight: FontWeight.w900),
                const SizedBox(height: 18),
                DropdownButtonFormField<String>(
                    isExpanded: true,
                    decoration: const InputDecoration(labelText: 'Cinema'),
                    items: [
                      for (final c in widget.cinemas)
                        DropdownMenuItem(value: c.id, child: Text(c.name))
                    ],
                    onChanged: saving
                        ? null
                        : (value) => setState(() => cinema = value)),
                const SizedBox(height: 12),
                DropdownButtonFormField<String>(
                    isExpanded: true,
                    decoration: const InputDecoration(labelText: 'Film'),
                    items: [
                      for (final f in widget.films)
                        DropdownMenuItem(value: f.id, child: Text(f.name))
                    ],
                    onChanged: saving
                        ? null
                        : (value) => setState(() => film = value)),
                const SizedBox(height: 12),
                TextField(
                    controller: room,
                    decoration: const InputDecoration(labelText: 'Room')),
                const SizedBox(height: 12),
                OutlinedButton.icon(
                    icon: const Icon(Icons.calendar_today),
                    label: Text(dateValue),
                    onPressed: saving
                        ? null
                        : () async {
                            final day = await showDatePicker(
                                context: context,
                                initialDate: date,
                                firstDate: DateUtils.dateOnly(DateTime.now()),
                                lastDate: DateTime.now()
                                    .add(const Duration(days: 365)));
                            if (mounted && day != null)
                              setState(() => date = day);
                          }),
                const SizedBox(height: 12),
                Row(children: [
                  Expanded(
                      child:
                          timeButton('Starts', start, (time) => start = time)),
                  const SizedBox(width: 10),
                  Expanded(
                      child: timeButton('Ends', end, (time) => end = time)),
                ]),
                const SizedBox(height: 12),
                TextField(
                    controller: price,
                    keyboardType: TextInputType.number,
                    decoration:
                        const InputDecoration(labelText: 'Price (TND)')),
                const SizedBox(height: 20),
                if (error != null)
                  Padding(
                      padding: const EdgeInsets.only(bottom: 10),
                      child: kText(error!, color: redHot)),
                kPrimaryButton(saving ? 'SAVING…' : 'SAVE SHOWTIME',
                    saving ? null : _save),
                const SizedBox(height: 20),
              ])),
        ),
      );

  Widget timeButton(
          String label, TimeOfDay value, ValueChanged<TimeOfDay> onPick) =>
      OutlinedButton(
          onPressed: saving
              ? null
              : () async {
                  final selected = await showTimePicker(
                      context: context, initialTime: value);
                  if (mounted && selected != null)
                    setState(() => onPick(selected));
                },
          child: Text('$label ${_timeValue(value)}'));
}

class ManagerCinemasTab extends StatelessWidget {
  const ManagerCinemasTab();
  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
            title: kText('Your cinemas', size: 22, weight: FontWeight.w900)),
        body: StreamBuilder(
          stream: watchCinemas(),
          builder: (_, snap) {
            final venues = _owned(snap.data ?? []);
            return ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: venues.length,
              itemBuilder: (_, i) {
                final c = venues[i];
                return Card(
                    child: ListTile(
                  leading: Image.asset(c.image,
                      width: 46,
                      height: 46,
                      fit: BoxFit.contain,
                      errorBuilder: (_, __, ___) =>
                          const Icon(Icons.theaters, color: muted)),
                  title: kText(c.name, size: 13, weight: FontWeight.w800),
                  subtitle: kText('${c.address} · ${c.description}',
                      size: 11, color: muted, maxLines: 1),
                  trailing: kText(c.status,
                      size: 10,
                      color: c.status == 'Accepted' ? Colors.green : gold),
                ));
              },
            );
          },
        ),
      );
}

// ---------------------------------------------------------------------------
// Administrator
// ---------------------------------------------------------------------------

class AdminHome extends StatefulWidget {
  const AdminHome({super.key});
  @override
  State<AdminHome> createState() => _AdminHomeState();
}

class _AdminHomeState extends State<AdminHome> {
  int _tab = 0;

  @override
  Widget build(BuildContext context) => Scaffold(
        body: IndexedStack(index: _tab, children: const [
          AdminUsersTab(),
          CinemaApprovalsTab(),
          AdminCatalogueTab(),
        ]),
        bottomNavigationBar: NavigationBar(
          selectedIndex: _tab,
          onDestinationSelected: (i) => setState(() => _tab = i),
          backgroundColor: surface,
          indicatorColor: red,
          destinations: const [
            NavigationDestination(
                icon: Icon(Icons.people_outline),
                selectedIcon: Icon(Icons.people),
                label: 'Users'),
            NavigationDestination(
                icon: Icon(Icons.fact_check_outlined),
                selectedIcon: Icon(Icons.fact_check),
                label: 'Approvals'),
            NavigationDestination(
                icon: Icon(Icons.movie_outlined),
                selectedIcon: Icon(Icons.movie),
                label: 'Films'),
          ],
        ),
      );
}

class AdminUsersTab extends StatelessWidget {
  const AdminUsersTab();
  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
            actions: [_signOut()],
            title:
                Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              kText('RAKCHA WORKSPACE', size: 11, weight: FontWeight.w900),
              kText('Administration', size: 13, color: muted),
            ])),
        body: StreamBuilder(
          stream: FirebaseFirestore.instance.collection('users').snapshots(),
          builder: (_, snap) {
            final users = snap.data?.docs ?? [];
            return SingleChildScrollView(
              padding: const EdgeInsets.all(20),
              child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    kEyebrow('Administration'),
                    kText('USERS', size: 26, weight: FontWeight.w900),
                    const SizedBox(height: 8),
                    kText(
                        'Manage accounts and review the cinema requests submitted by managers.',
                        size: 12,
                        color: muted),
                    const SizedBox(height: 20),
                    Row(children: [
                      Expanded(
                          child: kKpi(
                              '${users.length}', 'Accounts', Icons.people)),
                      const SizedBox(width: 8),
                      Expanded(
                          child: kKpi(
                              '${users.where((d) => d.data()!['role'] == 'admin').length}',
                              'Admins',
                              Icons.admin_panel_settings)),
                      const SizedBox(width: 8),
                      Expanded(
                          child: kKpi(
                              '${users.where((d) => d.data()!['role'] == 'manager').length}',
                              'Managers',
                              Icons.theaters)),
                    ]),
                    const SizedBox(height: 24),
                    kSectionHead('Account directory', 'All accounts'),
                    const SizedBox(height: 12),
                    ...users.map((d) => Card(
                            child: ListTile(
                          leading: const CircleAvatar(
                              child: Icon(Icons.person, size: 18)),
                          title: kText(d.data()!['display_name'] ?? 'User',
                              size: 13, weight: FontWeight.w800),
                          subtitle: kText(d.data()!['email'] ?? '',
                              size: 11, color: muted),
                          trailing: Container(
                            padding: const EdgeInsets.symmetric(
                                horizontal: 8, vertical: 4),
                            decoration: BoxDecoration(
                                color: surface2,
                                borderRadius: BorderRadius.circular(999)),
                            child: kText(d.data()!['role'] ?? 'client',
                                size: 9,
                                color: redHot,
                                weight: FontWeight.w800),
                          ),
                        ))),
                  ]),
            );
          },
        ),
      );
}

class CinemaApprovalsTab extends StatefulWidget {
  const CinemaApprovalsTab();
  @override
  State<CinemaApprovalsTab> createState() => CinemaApprovalsTabState();
}

class CinemaApprovalsTabState extends State<CinemaApprovalsTab> {
  String? _message;

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
            title:
                kText('Cinema approvals', size: 20, weight: FontWeight.w900)),
        body: StreamBuilder(
          stream: watchCinemas(),
          builder: (_, snap) {
            final cinemas = snap.data ?? [];
            final pending =
                cinemas.where((c) => c.status == 'Pending').toList();
            return SingleChildScrollView(
              padding: const EdgeInsets.all(20),
              child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    kEyebrow('Administration'),
                    kText('CINEMA APPROVALS',
                        size: 24, weight: FontWeight.w900),
                    const SizedBox(height: 8),
                    kText(
                        'Review new cinema requests. Accepted venues appear in the public programme.',
                        size: 12,
                        color: muted),
                    const SizedBox(height: 20),
                    Row(children: [
                      Expanded(
                          child: kKpi('${pending.length}', 'Pending',
                              Icons.hourglass_empty)),
                      const SizedBox(width: 8),
                      Expanded(
                          child: kKpi('${cinemas.length - pending.length}',
                              'Approved', Icons.check_circle)),
                    ]),
                    if (_message != null) ...[
                      const SizedBox(height: 16),
                      kText(_message!, size: 12, color: Colors.green),
                    ],
                    const SizedBox(height: 24),
                    kSectionHead('Requests', '${pending.length} pending'),
                    const SizedBox(height: 12),
                    if (pending.isEmpty)
                      kText('All cinema requests are reviewed.', color: muted),
                    ...pending.map((c) => Card(
                            child: ListTile(
                          leading: Image.asset(c.image,
                              width: 44,
                              height: 44,
                              fit: BoxFit.contain,
                              errorBuilder: (_, __, ___) =>
                                  const Icon(Icons.theaters, color: muted)),
                          title:
                              kText(c.name, size: 13, weight: FontWeight.w800),
                          subtitle: kText(c.address, size: 11, color: muted),
                          trailing: FilledButton(
                              onPressed: () => _approve(c),
                              child: const Text('Review')),
                        ))),
                  ]),
            );
          },
        ),
      );

  Future<void> _approve(CinemaData cinema) async {
    final confirmed = await showDialog<bool>(
        context: context,
        builder: (dialog) => AlertDialog(
              title: Text('Approve ${cinema.name}?'),
              content: Text(
                  '${cinema.address}\n\n${cinema.description}\n\nOnce approved, this venue is visible in the cinema directory.'),
              actions: [
                TextButton(
                    onPressed: () => Navigator.pop(dialog),
                    child: const Text('Cancel')),
                FilledButton(
                    onPressed: () => Navigator.pop(dialog, true),
                    child: const Text('Approve cinema')),
              ],
            ));
    if (confirmed != true || !mounted) return;
    try {
      await setCinemaStatus(cinema.id, 'Accepted');
      if (mounted) setState(() => _message = '${cinema.name} approved');
    } catch (_) {
      if (mounted)
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
              content: Text('Could not approve the cinema. Try again.')),
        );
    }
  }
}

class AdminCatalogueTab extends StatelessWidget {
  const AdminCatalogueTab();
  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
            title: kText('Catalogue', size: 22, weight: FontWeight.w900)),
        body: StreamBuilder(
          stream: watchFilms(),
          builder: (_, snap) => GridView.builder(
            padding: const EdgeInsets.all(16),
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2,
                mainAxisSpacing: 12,
                crossAxisSpacing: 12,
                childAspectRatio: .72),
            itemCount: (snap.data ?? []).length,
            itemBuilder: (_, i) {
              final f = snap.data![i];
              return GestureDetector(
                  onTap: () => Navigator.of(context).push(MaterialPageRoute(
                      builder: (_) => AdminFilmDetail(film: f))),
                  child: Card(
                      child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                        Expanded(
                            child: Image.asset(f.image,
                                width: double.infinity,
                                fit: BoxFit.cover,
                                errorBuilder: (_, __, ___) =>
                                    const Icon(Icons.movie, color: muted))),
                        Padding(
                            padding: const EdgeInsets.all(8),
                            child: kText(f.name,
                                size: 11,
                                weight: FontWeight.w800,
                                maxLines: 1)),
                      ])));
            },
          ),
        ),
      );
}

class AdminFilmDetail extends StatelessWidget {
  final FilmData film;
  const AdminFilmDetail({super.key, required this.film});

  @override
  Widget build(BuildContext context) => Scaffold(
        body: SingleChildScrollView(
          padding: const EdgeInsets.only(bottom: 28),
          child:
              Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Stack(children: [
              Image.asset(film.image,
                  height: 300,
                  width: double.infinity,
                  fit: BoxFit.cover,
                  errorBuilder: (_, __, ___) =>
                      Container(height: 300, color: surface2)),
              Positioned(
                left: 12,
                top: 12,
                child: CircleAvatar(
                  backgroundColor: Colors.black54,
                  child: IconButton(
                      tooltip: 'Back',
                      icon: const Icon(Icons.arrow_back, color: Colors.white),
                      onPressed: () => Navigator.pop(context)),
                ),
              ),
            ]),
            Padding(
              padding: const EdgeInsets.all(20),
              child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    kEyebrow(film.categories.join(' · ')),
                    const SizedBox(height: 6),
                    kText(film.name, size: 26, weight: FontWeight.w900),
                    const SizedBox(height: 12),
                    Wrap(spacing: 8, children: [
                      _chip(film.year),
                      _chip('${film.price} TND'),
                      _chip('Rated ${film.rating.toStringAsFixed(1)}'),
                    ]),
                    const SizedBox(height: 18),
                    kEyebrow('Synopsis'),
                    const SizedBox(height: 6),
                    kText(film.description, size: 13, color: muted),
                    if (film.actors.isNotEmpty) ...[
                      const SizedBox(height: 18),
                      kEyebrow('Cast'),
                      const SizedBox(height: 6),
                      kText(film.actors.join(' · '), size: 13, color: muted),
                    ],
                    const SizedBox(height: 22),
                    StreamBuilder(
                      stream: watchShowtimes(),
                      builder: (context, snap) => kKpi(
                          '${(snap.data ?? []).where((s) => s.filmId == film.id).length}',
                          'Scheduled screenings',
                          Icons.event),
                    ),
                  ]),
            ),
          ]),
        ),
      );

  Widget _chip(String label) => Container(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
        decoration: BoxDecoration(
            color: surface2,
            borderRadius: BorderRadius.circular(999),
            border: Border.all(color: line)),
        child: kText(label, size: 10, weight: FontWeight.w700),
      );
}
