import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:url_launcher/url_launcher.dart';

import 'core.dart';

void openProfile(BuildContext context) => Navigator.of(context).push(
      MaterialPageRoute(builder: (_) => const ProfilePage()),
    );

Widget profileAvatar() => Builder(
    builder: (context) => IconButton(
          tooltip: 'Your profile',
          icon: const CircleAvatar(
            radius: 17,
            backgroundColor: surface2,
            child: Icon(Icons.person, size: 18, color: redHot),
          ),
          onPressed: () => openProfile(context),
        ));

class ClientHome extends StatefulWidget {
  const ClientHome({super.key});

  @override
  State<ClientHome> createState() => _ClientHomeState();
}

class _ClientHomeState extends State<ClientHome> {
  int _tab = 0;

  @override
  Widget build(BuildContext context) {
    final pages = [
      const DiscoverTab(),
      const CinemasTab(),
      const ShopTab(),
      const SeriesTab(),
      const TicketsTab(),
    ];
    return Scaffold(
      body: IndexedStack(index: _tab, children: pages),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _tab,
        onDestinationSelected: (i) => setState(() => _tab = i),
        backgroundColor: surface,
        indicatorColor: red,
        destinations: const [
          NavigationDestination(
              icon: Icon(Icons.movie_outlined),
              selectedIcon: Icon(Icons.movie),
              label: 'Discover'),
          NavigationDestination(
              icon: Icon(Icons.theaters_outlined),
              selectedIcon: Icon(Icons.theaters),
              label: 'Cinemas'),
          NavigationDestination(
              icon: Icon(Icons.shopping_bag_outlined),
              selectedIcon: Icon(Icons.shopping_bag),
              label: 'Shop'),
          NavigationDestination(
              icon: Icon(Icons.video_collection_outlined),
              selectedIcon: Icon(Icons.video_collection),
              label: 'Series'),
          NavigationDestination(
              icon: Icon(Icons.confirmation_number_outlined),
              selectedIcon: Icon(Icons.confirmation_number),
              label: 'Tickets'),
        ],
      ),
    );
  }
}

// ---------------------------------------------------------------------------

class DiscoverTab extends StatelessWidget {
  const DiscoverTab();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: RichText(
            text: const TextSpan(children: [
          TextSpan(
              text: 'RAKCHA',
              style: TextStyle(
                  fontWeight: FontWeight.w900,
                  fontSize: 19,
                  color: Colors.white)),
        ])),
        actions: [
          Padding(
              padding: const EdgeInsets.only(right: 12), child: profileAvatar())
        ],
      ),
      body: StreamBuilder(
        stream: watchFilms(),
        builder: (context, filmSnap) {
          final films = filmSnap.data ?? [];
          return StreamBuilder(
            stream: watchCinemas(),
            builder: (context, cinemaSnap) {
              final cinemas = (cinemaSnap.data ?? [])
                  .where((c) => c.status == 'Accepted')
                  .toList();
              if (filmSnap.connectionState == ConnectionState.waiting) {
                return const Center(
                    child: CircularProgressIndicator(color: red));
              }
              return SingleChildScrollView(
                padding: const EdgeInsets.only(bottom: 28),
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      HeroCarousel(films: films.take(3).toList()),
                      const SizedBox(height: 26),
                      Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 20),
                          child: kSectionHead('The programme', 'Now showing')),
                      const SizedBox(height: 12),
                      SizedBox(
                        height: 248,
                        child: ListView.separated(
                          padding: const EdgeInsets.symmetric(horizontal: 20),
                          scrollDirection: Axis.horizontal,
                          itemCount: films.length,
                          separatorBuilder: (_, __) =>
                              const SizedBox(width: 14),
                          itemBuilder: (_, i) => GestureDetector(
                            onTap: () => Navigator.of(context).push(
                                MaterialPageRoute(
                                    builder: (_) =>
                                        FilmDetailScreen(film: films[i]))),
                            child: kPoster(films[i], height: 180, width: 124),
                          ),
                        ),
                      ),
                      const SizedBox(height: 22),
                      Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 20),
                          child: kSectionHead(
                              'Good to know', 'How booking works')),
                      const SizedBox(height: 12),
                      Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 20),
                        child: Column(children: const [
                          BookingStep('01', 'Pick a film',
                              'Explore the catalogue and open a card.'),
                          BookingStep('02', 'Choose a showtime',
                              'Cinema, date and time together.'),
                          BookingStep('03', 'Take your seats',
                              'See the room and select your places.'),
                        ]),
                      ),
                      const SizedBox(height: 22),
                      Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 20),
                          child: kSectionHead('Where we play', 'Cinemas')),
                      const SizedBox(height: 12),
                      SizedBox(
                        height: 130,
                        child: ListView.separated(
                          padding: const EdgeInsets.symmetric(horizontal: 20),
                          scrollDirection: Axis.horizontal,
                          itemCount: cinemas.length,
                          separatorBuilder: (_, __) =>
                              const SizedBox(width: 12),
                          itemBuilder: (_, i) => SizedBox(
                              width: 186, child: CinemaCard(cinemas[i])),
                        ),
                      ),
                    ]),
              );
            },
          );
        },
      ),
    );
  }
}

class HeroCarousel extends StatefulWidget {
  final List<FilmData> films;
  const HeroCarousel({required this.films});

  @override
  State<HeroCarousel> createState() => HeroCarouselState();
}

class HeroCarouselState extends State<HeroCarousel> {
  final _controller = PageController();
  int _page = 0;

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (widget.films.isEmpty) return const SizedBox(height: 300);
    return Column(children: [
      SizedBox(
        height: 330,
        child: PageView.builder(
          controller: _controller,
          onPageChanged: (i) => setState(() => _page = i),
          itemCount: widget.films.length,
          itemBuilder: (_, i) {
            final f = widget.films[i];
            return Stack(fit: StackFit.expand, children: [
              Image.asset(f.image,
                  fit: BoxFit.cover,
                  errorBuilder: (_, __, ___) => Container(color: surface2)),
              Container(
                decoration: const BoxDecoration(
                    gradient: LinearGradient(
                        begin: Alignment.topCenter,
                        end: Alignment.bottomCenter,
                        colors: [Colors.transparent, bg])),
              ),
              Positioned(
                left: 20,
                right: 20,
                bottom: 18,
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      kEyebrow('Featured this week'),
                      const SizedBox(height: 6),
                      kText(f.name,
                          size: 25, weight: FontWeight.w900, maxLines: 2),
                      const SizedBox(height: 8),
                      Row(children: [
                        _chip(f.year),
                        const SizedBox(width: 6),
                        _chip('${f.price} TND'),
                      ]),
                      const SizedBox(height: 14),
                      SizedBox(
                        width: double.infinity,
                        child: FilledButton.icon(
                          onPressed: () => Navigator.of(context).push(
                              MaterialPageRoute(
                                  builder: (_) => SeatBookingScreen(film: f))),
                          icon:
                              const Icon(Icons.local_fire_department, size: 18),
                          label: const Text(
                            'BOOK TICKETS',
                            style: TextStyle(
                                fontSize: 14, fontWeight: FontWeight.w800),
                          ),
                          style: FilledButton.styleFrom(
                            backgroundColor: red,
                            foregroundColor: Colors.white,
                            minimumSize: const Size.fromHeight(54),
                            shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(14)),
                          ),
                        ),
                      ),
                    ]),
              ),
            ]);
          },
        ),
      ),
      const SizedBox(height: 10),
      Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: List.generate(
              widget.films.length,
              (i) => Container(
                  margin: const EdgeInsets.symmetric(horizontal: 3),
                  width: _page == i ? 18 : 6,
                  height: 6,
                  decoration: BoxDecoration(
                      color: _page == i ? red : line,
                      borderRadius: BorderRadius.circular(3))))),
    ]);
  }

  Widget _chip(String text) => Container(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
        decoration: BoxDecoration(
            color: Colors.black54,
            borderRadius: BorderRadius.circular(999),
            border: Border.all(color: line)),
        child: kText(text, size: 10, weight: FontWeight.w700),
      );
}

class BookingStep extends StatelessWidget {
  final String n, title, body;
  const BookingStep(this.n, this.title, this.body);
  @override
  Widget build(BuildContext context) => Container(
        margin: const EdgeInsets.only(bottom: 10),
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
            color: surface,
            borderRadius: BorderRadius.circular(14),
            border: Border.all(color: line)),
        child: Row(children: [
          kText(n, size: 16, color: redHot, weight: FontWeight.w900),
          const SizedBox(width: 14),
          Expanded(
              child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                kText(title, size: 13, weight: FontWeight.w800),
                const SizedBox(height: 3),
                kText(body, size: 11, color: muted, maxLines: 2),
              ])),
        ]),
      );
}

class CinemaCard extends StatelessWidget {
  final CinemaData cinema;
  const CinemaCard(this.cinema);
  @override
  Widget build(BuildContext context) => Card(
        child: Padding(
            padding: const EdgeInsets.all(12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                ClipRRect(
                  borderRadius: BorderRadius.circular(8),
                  child: SizedBox(
                    height: 68,
                    width: double.infinity,
                    child: Padding(
                      padding: const EdgeInsets.all(8),
                      child: Image.asset(cinema.image,
                          fit: BoxFit.contain,
                          errorBuilder: (_, __, ___) =>
                              _fallback(Icons.theaters)),
                    ),
                  ),
                ),
                const SizedBox(height: 8),
                kText(cinema.name,
                    size: 12, weight: FontWeight.w800, maxLines: 1),
                kText(cinema.address, size: 10, color: muted),
              ],
            )),
      );
}

// ---------------------------------------------------------------------------
// Film detail
// ---------------------------------------------------------------------------

class FilmDetailScreen extends StatelessWidget {
  final FilmData film;
  const FilmDetailScreen({required this.film});

  @override
  Widget build(BuildContext context) {
    final uid = FirebaseAuth.instance.currentUser!.uid;
    // Prefer IDs stored with the film; fall back to known IDs for existing
    // catalogue rows that predate those fields.
    final legacy = switch (film.name) {
      'Spider-Man: No Way Home' => ('JfVOs4VSpmA', 'tt10872600'),
      'Avengers: Infinity War' => ('6ZfuNTqbHE8', 'tt4154756'),
      'Oppenheimer' => ('uYPbbksJxIg', 'tt15398776'),
      _ => ('', ''),
    };
    final trailer = film.trailerId.isNotEmpty ? film.trailerId : legacy.$1;
    final imdb = film.imdbId.isNotEmpty ? film.imdbId : legacy.$2;
    final hasLinks = trailer.isNotEmpty && imdb.isNotEmpty;
    return Scaffold(
      body: SingleChildScrollView(
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Stack(children: [
            Image.asset(film.image,
                height: 300,
                width: double.infinity,
                fit: BoxFit.cover,
                errorBuilder: (_, __, ___) =>
                    Container(height: 300, color: surface2)),
            SafeArea(
                child: Padding(
                    padding: const EdgeInsets.all(14),
                    child: CircleAvatar(
                      backgroundColor: Colors.black54,
                      child: IconButton(
                          icon:
                              const Icon(Icons.arrow_back, color: Colors.white),
                          onPressed: () => Navigator.pop(context)),
                    ))),
          ]),
          Padding(
              padding: const EdgeInsets.all(20),
              child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    kEyebrow(film.categories.join(' · ')),
                    const SizedBox(height: 6),
                    kText(film.name, size: 26, weight: FontWeight.w900),
                    const SizedBox(height: 6),
                    Row(children: [
                      _chip(film.year),
                      const SizedBox(width: 6),
                      _chip('${film.price} TND'),
                      const SizedBox(width: 6),
                      StreamBuilder<bool>(
                        stream: isBookmarked(uid, film.id),
                        builder: (_, snap) => IconButton(
                          icon: Icon(Icons.bookmark,
                              color: snap.data == true ? redHot : muted),
                          onPressed: () =>
                              toggleBookmark(uid, film.id, snap.data != true),
                        ),
                      ),
                    ]),
                    const SizedBox(height: 16),
                    kText(film.description, size: 13, color: muted),
                    const SizedBox(height: 18),
                    kEyebrow('Your rating'),
                    const SizedBox(height: 6),
                    StreamBuilder<int>(
                      stream: watchRating(uid, film.id),
                      builder: (context, snapshot) {
                        final stars = snapshot.data ?? 0;
                        return Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                                  children: List.generate(
                                      5,
                                      (i) => IconButton(
                                            tooltip: 'Rate ${i + 1} out of 5',
                                            padding: EdgeInsets.zero,
                                            icon: Icon(
                                                i < stars
                                                    ? Icons.star
                                                    : Icons.star_border,
                                                color: gold,
                                                size: 26),
                                            onPressed: () =>
                                                rateFilm(uid, film.id, i + 1),
                                          ))),
                              kText(
                                  stars == 0
                                      ? 'Tap a star to rate this film'
                                      : 'Your rating: $stars out of 5',
                                  size: 12,
                                  color: muted),
                            ]);
                      },
                    ),
                    if (film.actors.isNotEmpty) ...[
                      const SizedBox(height: 18),
                      kEyebrow('Cast'),
                      const SizedBox(height: 5),
                      kText(film.actors.join(' · '), size: 13, color: muted),
                    ],
                    const SizedBox(height: 18),
                    if (hasLinks)
                      Row(children: [
                        Expanded(
                            child: kOutlineButton(
                                'Trailer',
                                () => _mediaSheet(
                                    context,
                                    Uri.https('www.youtube.com', '/watch',
                                        {'v': trailer})),
                                icon: Icons.play_circle_outline)),
                        const SizedBox(width: 10),
                        Expanded(
                            child: kOutlineButton(
                                'IMDb',
                                () => _mediaSheet(context,
                                    Uri.https('www.imdb.com', '/title/$imdb/'),
                                    imdb: true),
                                icon: Icons.qr_code_2)),
                      ]),
                    const SizedBox(height: 14),
                    SizedBox(
                        width: double.infinity,
                        child: kButton(
                            'RESERVE SEATS',
                            () => Navigator.of(context).push(MaterialPageRoute(
                                builder: (_) =>
                                    SeatBookingScreen(film: film))))),
                  ])),
        ]),
      ),
    );
  }

  Widget _chip(String text) => Container(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
        decoration: BoxDecoration(
            color: surface2,
            borderRadius: BorderRadius.circular(999),
            border: Border.all(color: line)),
        child: kText(text, size: 10, color: muted, weight: FontWeight.w700),
      );

  void _mediaSheet(BuildContext context, Uri uri, {bool imdb = false}) =>
      showRakchaSheet(
        context: context,
        builder: (sheet) => SafeArea(
            child: SizedBox(
          width: double.infinity,
          child: Padding(
            padding: const EdgeInsets.fromLTRB(20, 4, 20, 24),
            child: Column(mainAxisSize: MainAxisSize.min, children: [
              kEyebrow(imdb ? 'IMDb' : 'Official trailer'),
              const SizedBox(height: 8),
              kText(film.name,
                  size: 18,
                  weight: FontWeight.w800,
                  textAlign: TextAlign.center),
              const SizedBox(height: 18),
              if (imdb)
                QrImageView(
                    data: uri.toString(),
                    size: 176,
                    backgroundColor: Colors.white)
              else
                kPoster(film, height: 154, width: 108, showInfo: false),
              const SizedBox(height: 16),
              if (imdb)
                kText('Scan the code or open the film’s IMDb page.',
                    size: 12, color: muted),
              const SizedBox(height: 8),
              SizedBox(
                width: double.infinity,
                child:
                    kButton(imdb ? 'OPEN IMDb' : 'WATCH ON YOUTUBE', () async {
                  if (!await launchUrl(uri,
                          mode: LaunchMode.externalApplication) &&
                      context.mounted) {
                    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
                        content: Text('Could not open this link.')));
                  }
                }),
              ),
            ]),
          ),
        )),
      );
}

// ---------------------------------------------------------------------------
// Booking
// ---------------------------------------------------------------------------

class SeatBookingScreen extends StatefulWidget {
  final FilmData film;
  const SeatBookingScreen({required this.film});

  @override
  State<SeatBookingScreen> createState() => SeatBookingScreenState();
}

class SeatBookingScreenState extends State<SeatBookingScreen> {
  CinemaData? _cinema;
  ShowtimeData? _showtime;
  final Set<String> _seats = {};
  bool _plan = false;
  bool _done = false;
  bool _saving = false;

  @override
  Widget build(BuildContext context) {
    if (_done) return _success(context);
    return Scaffold(
      appBar: AppBar(
          title: kText('Book · ${widget.film.name}',
              size: 16, weight: FontWeight.w800)),
      body: StreamBuilder(
        stream: watchCinemas(),
        builder: (context, cinemaSnap) {
          final cinemas = cinemaSnap.data ?? [];
          return StreamBuilder(
            stream: watchShowtimes(),
            builder: (context, showSnap) {
              final showtimes = (showSnap.data ?? [])
                  .where((s) => s.filmId == widget.film.id)
                  .toList();
              final availableCinemas = cinemas.where((c) =>
                  c.status == 'Accepted' &&
                  showtimes.any((show) => show.cinemaId == c.id));
              return SingleChildScrollView(
                padding: EdgeInsets.fromLTRB(
                    20, 20, 20, MediaQuery.of(context).padding.bottom + 20),
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      kEyebrow('Step 1 of 3'),
                      kText('Choose a cinema',
                          size: 20, weight: FontWeight.w800),
                      const SizedBox(height: 12),
                      if (showSnap.connectionState == ConnectionState.waiting)
                        const LinearProgressIndicator()
                      else if (availableCinemas.isEmpty)
                        kText('No showtimes are listed for this film yet.',
                            color: muted),
                      Wrap(
                          spacing: 8,
                          runSpacing: 8,
                          children: availableCinemas
                              .map((c) => ChoiceChip(
                                    label: kText(c.name, size: 11),
                                    selected: _cinema?.id == c.id,
                                    onSelected: (_) => setState(() {
                                      _cinema = c;
                                      _showtime = null;
                                      _seats.clear();
                                    }),
                                    selectedColor: red,
                                    backgroundColor: surface2,
                                  ))
                              .toList()),
                      if (_cinema != null) ...[
                        const SizedBox(height: 24),
                        kEyebrow('Step 2 of 3'),
                        kText('Pick a showtime',
                            size: 20, weight: FontWeight.w800),
                        const SizedBox(height: 12),
                        Wrap(
                            spacing: 8,
                            runSpacing: 8,
                            children: showtimes
                                .where((s) => s.cinemaId == _cinema!.id)
                                .map((s) => ChoiceChip(
                                      label: kText('${s.date} · ${s.hd}',
                                          size: 11),
                                      selected: _showtime?.id == s.id,
                                      onSelected: (_) => setState(() {
                                        _showtime = s;
                                        _seats.clear();
                                      }),
                                      selectedColor: red,
                                      backgroundColor: surface2,
                                    ))
                                .toList()),
                      ],
                      if (_showtime != null) ...[
                        const SizedBox(height: 24),
                        Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              kEyebrow('Step 3 of 3'),
                              ToggleButtons(
                                isSelected: [!_plan, _plan],
                                onPressed: (i) =>
                                    setState(() => _plan = i == 1),
                                borderRadius: BorderRadius.circular(10),
                                selectedColor: Colors.white,
                                fillColor: red,
                                color: muted,
                                children: const [
                                  Padding(
                                      padding:
                                          EdgeInsets.symmetric(horizontal: 10),
                                      child: Text('Room',
                                          style: TextStyle(fontSize: 11))),
                                  Padding(
                                      padding:
                                          EdgeInsets.symmetric(horizontal: 10),
                                      child: Text('Plan',
                                          style: TextStyle(fontSize: 11)))
                                ],
                              ),
                            ]),
                        const SizedBox(height: 8),
                        kText('Choose your seats',
                            size: 20, weight: FontWeight.w800),
                        const SizedBox(height: 10),
                        Container(
                          margin: const EdgeInsets.symmetric(vertical: 14),
                          height: 6,
                          decoration: BoxDecoration(
                              color: red.withValues(alpha: .5),
                              borderRadius: BorderRadius.circular(3)),
                        ),
                        Center(child: kText('SCREEN', size: 9, color: muted)),
                        const SizedBox(height: 16),
                        AnimatedContainer(
                          duration: const Duration(milliseconds: 350),
                          transform: Matrix4.identity()
                            ..setEntry(3, 2, 0.002)
                            ..rotateX(_plan ? 0 : 0.22),
                          child: GridView.builder(
                            shrinkWrap: true,
                            physics: const NeverScrollableScrollPhysics(),
                            gridDelegate:
                                const SliverGridDelegateWithFixedCrossAxisCount(
                                    crossAxisCount: 8,
                                    mainAxisSpacing: 8,
                                    crossAxisSpacing: 8),
                            itemCount: 40,
                            itemBuilder: (_, index) {
                              final id =
                                  '${String.fromCharCode(65 + index ~/ 8)}${index % 8 + 1}';
                              final chosen = _seats.contains(id);
                              return IconButton(
                                tooltip: chosen
                                    ? 'Remove seat $id'
                                    : 'Select seat $id',
                                padding: EdgeInsets.zero,
                                icon: Icon(Icons.event_seat,
                                    size: 30, color: chosen ? redHot : gold),
                                onPressed: _saving
                                    ? null
                                    : () => setState(() => chosen
                                        ? _seats.remove(id)
                                        : _seats.add(id)),
                              );
                            },
                          ),
                        ),
                        const SizedBox(height: 20),
                        Container(
                          padding: const EdgeInsets.all(16),
                          decoration: BoxDecoration(
                              color: surface,
                              borderRadius: BorderRadius.circular(14),
                              border: Border.all(color: line)),
                          child: Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Expanded(
                                    child: kText(
                                        _seats.isEmpty
                                            ? 'No seats selected'
                                            : '${_seats.length} seats · ${(_seats.length * _showtime!.price).toStringAsFixed(2)} TND',
                                        size: 13,
                                        weight: FontWeight.w700)),
                                const SizedBox(width: 8),
                                kButton(
                                    'REQUEST SEATS',
                                    _seats.isEmpty || _saving
                                        ? null
                                        : _confirm),
                              ]),
                        ),
                      ],
                    ]),
              );
            },
          );
        },
      ),
    );
  }

  Future<void> _confirm() async {
    setState(() => _saving = true);
    try {
      await createBooking(
        uid: FirebaseAuth.instance.currentUser!.uid,
        filmId: widget.film.id,
        filmName: widget.film.name,
        showtimeId: _showtime!.id,
        seats: _seats.toList(),
        total: _seats.length * _showtime!.price,
      );
      if (mounted) setState(() => _done = true);
    } catch (_) {
      if (mounted)
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
            content: Text('Could not save your seat request. Try again.')));
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  Widget _success(BuildContext context) => Scaffold(
        body: Center(
            child: Padding(
                padding: const EdgeInsets.all(30),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Icon(Icons.check_circle,
                        color: Colors.green, size: 72),
                    const SizedBox(height: 18),
                    kText('Seat request saved',
                        size: 24, weight: FontWeight.w900),
                    const SizedBox(height: 8),
                    kText('${_seats.length} seat(s) for ${widget.film.name}',
                        size: 13, color: muted, textAlign: TextAlign.center),
                    const SizedBox(height: 26),
                    SizedBox(
                        width: double.infinity,
                        child: kButton(
                            'BACK TO DISCOVER', () => Navigator.pop(context))),
                  ],
                ))),
      );
}

// ---------------------------------------------------------------------------

// Shared visual primitives for the catalogue tabs.
class _Pill extends StatelessWidget {
  final IconData icon;
  final String label;
  const _Pill({required this.icon, required this.label});

  @override
  Widget build(BuildContext context) => Container(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
        decoration: BoxDecoration(
          color: red.withValues(alpha: 0.85),
          borderRadius: BorderRadius.circular(20),
        ),
        child: Row(mainAxisSize: MainAxisSize.min, children: [
          Icon(icon, size: 12, color: Colors.white),
          const SizedBox(width: 5),
          kText(label, size: 9, color: Colors.white, weight: FontWeight.w800),
        ]),
      );
}

class _Banner extends StatelessWidget {
  final String image;
  final double height;
  final Widget overlay;
  final BoxFit fit;
  final EdgeInsets imagePadding;
  const _Banner({
    required this.image,
    required this.height,
    required this.overlay,
    this.fit = BoxFit.cover,
    this.imagePadding = EdgeInsets.zero,
  });

  @override
  Widget build(BuildContext context) => ClipRRect(
        borderRadius: BorderRadius.circular(20),
        child: SizedBox(
          height: height,
          width: double.infinity,
          child: Stack(fit: StackFit.expand, children: [
            Container(
              color: surface2,
              padding: imagePadding,
              child: Image.asset(
                image,
                fit: fit,
                errorBuilder: (_, __, ___) =>
                    const Icon(Icons.image, size: 40, color: muted),
              ),
            ),
            const DecoratedBox(
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  begin: Alignment.topCenter,
                  end: Alignment.bottomCenter,
                  colors: [Colors.transparent, Color(0xE60B0709)],
                  stops: [0.25, 1],
                ),
              ),
            ),
            Padding(padding: const EdgeInsets.all(18), child: overlay),
          ]),
        ),
      );
}

Widget _fallback(IconData icon) => Container(
      color: surface2,
      child: Center(child: Icon(icon, size: 34, color: muted)),
    );

class CinemasTab extends StatelessWidget {
  const CinemasTab();

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
          title: kText('Cinemas', size: 22, weight: FontWeight.w900),
          actions: [profileAvatar()],
        ),
        body: StreamBuilder(
          stream: watchCinemas(),
          builder: (context, snap) {
            final cinemas =
                (snap.data ?? []).where((c) => c.status == 'Accepted').toList();
            if (!snap.hasData) {
              return const Center(child: CircularProgressIndicator(color: red));
            }
            if (cinemas.isEmpty) {
              return Center(child: kText('No cinemas yet', color: muted));
            }
            final featured = cinemas.first;
            return SingleChildScrollView(
              padding: const EdgeInsets.only(bottom: 28),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Padding(
                    padding: const EdgeInsets.fromLTRB(20, 16, 20, 0),
                    child: _Banner(
                      image: featured.image,
                      height: 210,
                      overlay: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.end,
                        children: [
                          const _Pill(
                              icon: Icons.local_fire_department,
                              label: 'FEATURED VENUE'),
                          const SizedBox(height: 8),
                          kText(featured.name,
                              size: 23, weight: FontWeight.w900, maxLines: 2),
                          const SizedBox(height: 5),
                          Row(children: [
                            const Icon(Icons.place, size: 14, color: gold),
                            const SizedBox(width: 5),
                            kText(featured.address,
                                size: 12, color: Colors.white70),
                          ]),
                        ],
                      ),
                    ),
                  ),
                  const SizedBox(height: 26),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    child: kSectionHead('Explore', '${cinemas.length} venues',
                        trailing: const Icon(Icons.theaters, color: redHot)),
                  ),
                  const SizedBox(height: 12),
                  ListView.separated(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    itemCount: cinemas.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 12),
                    itemBuilder: (_, i) => _CinemaTile(cinemas[i]),
                  ),
                ],
              ),
            );
          },
        ),
      );
}

class _CinemaTile extends StatelessWidget {
  final CinemaData cinema;
  const _CinemaTile(this.cinema);

  @override
  Widget build(BuildContext context) => Card(
        clipBehavior: Clip.antiAlias,
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          SizedBox(
            height: 132,
            width: double.infinity,
            child: Stack(fit: StackFit.expand, children: [
              Container(
                color: surface2,
                padding: const EdgeInsets.all(18),
                child: Image.asset(cinema.image,
                    fit: BoxFit.contain,
                    errorBuilder: (_, __, ___) => _fallback(Icons.theaters)),
              ),
              const DecoratedBox(
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    colors: [Colors.transparent, Color(0x990B0709)],
                    stops: [0.4, 1],
                  ),
                ),
              ),
              Positioned(
                left: 14,
                bottom: 12,
                child: _Pill(icon: Icons.place, label: cinema.address),
              ),
            ]),
          ),
          Padding(
            padding: const EdgeInsets.fromLTRB(14, 12, 14, 14),
            child: Row(children: [
              Expanded(
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      kText(cinema.name,
                          size: 15, weight: FontWeight.w900, maxLines: 1),
                      const SizedBox(height: 4),
                      kText(cinema.description,
                          size: 11, color: muted, maxLines: 2),
                    ]),
              ),
              const SizedBox(width: 10),
              IconButton(
                tooltip: 'See showtimes',
                icon: const Icon(Icons.arrow_forward_ios,
                    color: redHot, size: 18),
                onPressed: () => Navigator.of(context).push(MaterialPageRoute(
                    builder: (_) => CinemaDetailScreen(cinema: cinema))),
              ),
            ]),
          ),
        ]),
      );
}

class CinemaDetailScreen extends StatelessWidget {
  final CinemaData cinema;
  const CinemaDetailScreen({super.key, required this.cinema});

  @override
  Widget build(BuildContext context) => Scaffold(
        body: SingleChildScrollView(
          padding: const EdgeInsets.only(bottom: 28),
          child:
              Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Stack(children: [
              _Banner(
                image: cinema.image,
                height: 260,
                fit: BoxFit.contain,
                imagePadding: const EdgeInsets.fromLTRB(40, 20, 40, 40),
                overlay: const SizedBox.shrink(),
              ),
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
              padding: const EdgeInsets.fromLTRB(20, 20, 20, 0),
              child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(children: [
                      _pill(Icons.place, cinema.address),
                      const SizedBox(width: 8),
                      _pill(Icons.check_circle, cinema.status),
                    ]),
                    const SizedBox(height: 12),
                    kText(cinema.name, size: 27, weight: FontWeight.w900),
                    const SizedBox(height: 8),
                    kText(
                        cinema.description.isEmpty
                            ? 'A Rakcha partner cinema.'
                            : cinema.description,
                        size: 13,
                        color: muted),
                    const SizedBox(height: 24),
                    kSectionHead('What’s on', 'Screenings'),
                    const SizedBox(height: 12),
                  ]),
            ),
            StreamBuilder(
              stream: watchFilms(),
              builder: (context, filmSnap) => StreamBuilder(
                stream: watchShowtimes(),
                builder: (context, showSnap) {
                  if (!filmSnap.hasData || !showSnap.hasData) {
                    return const Center(
                        child: CircularProgressIndicator(color: red));
                  }
                  final films = {for (final f in filmSnap.data!) f.id: f};
                  final shows = (showSnap.data ?? [])
                      .where((s) => s.cinemaId == cinema.id)
                      .toList()
                    ..sort((a, b) =>
                        '${a.date}${a.hd}'.compareTo('${b.date}${b.hd}'));
                  if (shows.isEmpty) {
                    return Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 20),
                      child:
                          kText('No screenings scheduled yet.', color: muted),
                    );
                  }
                  return ListView.separated(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    itemCount: shows.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 10),
                    itemBuilder: (_, i) {
                      final show = shows[i];
                      final film = films[show.filmId];
                      return Card(
                        child: Padding(
                          padding: const EdgeInsets.all(12),
                          child: Row(children: [
                            if (film != null)
                              ClipRRect(
                                borderRadius: BorderRadius.circular(10),
                                child: SizedBox(
                                  width: 58,
                                  height: 80,
                                  child: Image.asset(film.image,
                                      fit: BoxFit.cover,
                                      errorBuilder: (_, __, ___) =>
                                          _fallback(Icons.movie)),
                                ),
                              ),
                            const SizedBox(width: 12),
                            Expanded(
                                child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                  kText(film?.name ?? 'Film screening',
                                      size: 13, weight: FontWeight.w800),
                                  const SizedBox(height: 4),
                                  kText('${show.date} · ${show.hd}–${show.hf}',
                                      size: 11, color: muted),
                                  kText(
                                      '${show.room} · ${show.price.toStringAsFixed(0)} TND',
                                      size: 10,
                                      color: gold),
                                ])),
                            if (film != null)
                              IconButton(
                                tooltip: 'Reserve seats',
                                icon: const Icon(Icons.arrow_forward_ios,
                                    size: 16, color: redHot),
                                onPressed: () => Navigator.of(context).push(
                                    MaterialPageRoute(
                                        builder: (_) =>
                                            SeatBookingScreen(film: film))),
                              ),
                          ]),
                        ),
                      );
                    },
                  );
                },
              ),
            ),
          ]),
        ),
      );

  Widget _pill(IconData icon, String label) => Container(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
        decoration: BoxDecoration(
            color: surface2,
            borderRadius: BorderRadius.circular(999),
            border: Border.all(color: line)),
        child: Row(mainAxisSize: MainAxisSize.min, children: [
          Icon(icon, size: 12, color: redHot),
          const SizedBox(width: 5),
          kText(label, size: 10, weight: FontWeight.w700),
        ]),
      );
}

class ProductDetailScreen extends StatefulWidget {
  final ProductData product;
  final List<ProductData> catalogue;
  const ProductDetailScreen(
      {super.key, required this.product, required this.catalogue});

  @override
  State<ProductDetailScreen> createState() => ProductDetailScreenState();
}

class ProductDetailScreenState extends State<ProductDetailScreen> {
  int _qty = 1;
  final uid = FirebaseAuth.instance.currentUser!.uid;

  @override
  Widget build(BuildContext context) {
    final p = widget.product;
    return Scaffold(
      body: SingleChildScrollView(
        padding: const EdgeInsets.only(bottom: 28),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Stack(children: [
            _Banner(
              image: p.image,
              height: 320,
              overlay: const SizedBox.shrink(),
            ),
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
            Positioned(
              right: 16,
              bottom: 16,
              child: Container(
                padding:
                    const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                decoration: BoxDecoration(
                    color: red, borderRadius: BorderRadius.circular(999)),
                child:
                    kText('${p.price} TND', size: 15, weight: FontWeight.w900),
              ),
            ),
          ]),
          Padding(
            padding: const EdgeInsets.fromLTRB(20, 20, 20, 0),
            child:
                Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              _pill(Icons.inventory_2, p.category),
              const SizedBox(height: 12),
              kText(p.name, size: 26, weight: FontWeight.w900),
              const SizedBox(height: 10),
              kText(
                  p.description.isEmpty
                      ? 'A Rakcha collectible for your cinema shelf.'
                      : p.description,
                  size: 13,
                  color: muted),
              const SizedBox(height: 22),
              kEyebrow('Quantity'),
              const SizedBox(height: 8),
              Row(children: [
                IconButton.filled(
                  tooltip: 'Decrease quantity',
                  icon: const Icon(Icons.remove),
                  onPressed: () =>
                      setState(() => _qty = _qty > 1 ? _qty - 1 : 1),
                ),
                const SizedBox(width: 14),
                kText('$_qty', size: 20, weight: FontWeight.w900),
                const SizedBox(width: 14),
                IconButton.filled(
                  tooltip: 'Increase quantity',
                  icon: const Icon(Icons.add),
                  onPressed: () =>
                      setState(() => _qty = _qty < 99 ? _qty + 1 : 99),
                ),
              ]),
              const SizedBox(height: 24),
              SizedBox(
                width: double.infinity,
                child: kButton('ADD $_qty TO BASKET', () async {
                  await addToCart(uid, p, quantity: _qty);
                  if (context.mounted) {
                    ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                        content: Text('${p.name} added to your basket')));
                  }
                }, icon: Icons.add_shopping_cart),
              ),
              if (widget.catalogue.length > 1) ...[
                const SizedBox(height: 28),
                kSectionHead('More to explore', 'You may also like'),
                const SizedBox(height: 12),
                SizedBox(
                  height: 190,
                  child: ListView.separated(
                    scrollDirection: Axis.horizontal,
                    itemCount: widget.catalogue.length,
                    separatorBuilder: (_, __) => const SizedBox(width: 12),
                    itemBuilder: (_, i) {
                      final other = widget.catalogue[i];
                      return GestureDetector(
                        onTap: () => Navigator.of(context).pushReplacement(
                            MaterialPageRoute(
                                builder: (_) => ProductDetailScreen(
                                    product: other,
                                    catalogue: widget.catalogue))),
                        child: SizedBox(
                          width: 126,
                          child: Card(
                            clipBehavior: Clip.antiAlias,
                            child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Expanded(
                                    child: Image.asset(other.image,
                                        width: double.infinity,
                                        fit: BoxFit.cover,
                                        errorBuilder: (_, __, ___) =>
                                            _fallback(Icons.shopping_bag)),
                                  ),
                                  Padding(
                                    padding: const EdgeInsets.all(8),
                                    child: kText(other.name,
                                        size: 11, weight: FontWeight.w800),
                                  ),
                                ]),
                          ),
                        ),
                      );
                    },
                  ),
                ),
              ],
            ]),
          ),
        ]),
      ),
    );
  }

  Widget _pill(IconData icon, String label) => Container(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
        decoration: BoxDecoration(
            color: surface2,
            borderRadius: BorderRadius.circular(999),
            border: Border.all(color: line)),
        child: Row(mainAxisSize: MainAxisSize.min, children: [
          Icon(icon, size: 12, color: gold),
          const SizedBox(width: 5),
          kText(label, size: 10, weight: FontWeight.w700),
        ]),
      );
}

class SeriesDetailScreen extends StatelessWidget {
  final SeriesData series;
  final List<SeriesData> catalogue;
  const SeriesDetailScreen(
      {super.key, required this.series, required this.catalogue});

  @override
  Widget build(BuildContext context) {
    final s = series;
    return Scaffold(
      body: SingleChildScrollView(
        padding: const EdgeInsets.only(bottom: 28),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Stack(children: [
            _Banner(
              image: s.image,
              height: 330,
              overlay: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    const _Pill(
                        icon: Icons.auto_awesome, label: 'RAKCHA SERIES'),
                    const SizedBox(height: 8),
                    kText(s.name, size: 27, weight: FontWeight.w900),
                  ]),
            ),
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
            padding: const EdgeInsets.fromLTRB(20, 20, 20, 0),
            child:
                Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Row(children: [
                _info(Icons.person_outline, s.director),
                const SizedBox(width: 8),
                _info(Icons.public, s.country),
              ]),
              const SizedBox(height: 20),
              kSectionHead('About the series', 'Synopsis'),
              const SizedBox(height: 8),
              kText(
                  s.summary.isEmpty
                      ? 'No synopsis has been added for this series yet.'
                      : s.summary,
                  size: 13,
                  color: muted),
              if (catalogue.length > 1) ...[
                const SizedBox(height: 28),
                kSectionHead('Keep watching', 'More series'),
                const SizedBox(height: 12),
                SizedBox(
                  height: 210,
                  child: ListView.separated(
                    scrollDirection: Axis.horizontal,
                    itemCount: catalogue.length,
                    separatorBuilder: (_, __) => const SizedBox(width: 12),
                    itemBuilder: (_, i) {
                      final other = catalogue[i];
                      return GestureDetector(
                        onTap: () => Navigator.of(context).pushReplacement(
                            MaterialPageRoute(
                                builder: (_) => SeriesDetailScreen(
                                    series: other, catalogue: catalogue))),
                        child: SizedBox(
                          width: 140,
                          child: Card(
                            clipBehavior: Clip.antiAlias,
                            child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Expanded(
                                    child: Image.asset(other.image,
                                        width: double.infinity,
                                        fit: BoxFit.cover,
                                        errorBuilder: (_, __, ___) =>
                                            _fallback(Icons.tv)),
                                  ),
                                  Padding(
                                    padding: const EdgeInsets.all(8),
                                    child: kText(other.name,
                                        size: 11, weight: FontWeight.w800),
                                  ),
                                ]),
                          ),
                        ),
                      );
                    },
                  ),
                ),
              ],
            ]),
          ),
        ]),
      ),
    );
  }

  Widget _info(IconData icon, String label) => Container(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
        decoration: BoxDecoration(
            color: surface2,
            borderRadius: BorderRadius.circular(999),
            border: Border.all(color: line)),
        child: Row(mainAxisSize: MainAxisSize.min, children: [
          Icon(icon, size: 12, color: gold),
          const SizedBox(width: 5),
          kText(label.isEmpty ? '—' : label, size: 10, weight: FontWeight.w700),
        ]),
      );
}

class TicketDetailScreen extends StatelessWidget {
  final Map<String, dynamic> booking;
  const TicketDetailScreen({super.key, required this.booking});

  @override
  Widget build(BuildContext context) {
    final seats = List<String>.from(booking['seats'] ?? const []);
    final showtimeId = booking['seanceId'] ?? '';
    return Scaffold(
      appBar: AppBar(
          title: kText('Seat request', size: 19, weight: FontWeight.w900)),
      body: StreamBuilder(
        stream: watchShowtimes(),
        builder: (context, snap) {
          final show =
              (snap.data ?? []).where((s) => s.id == showtimeId).toList();
          return SingleChildScrollView(
            padding: const EdgeInsets.all(20),
            child:
                Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(20),
                  child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const _Pill(
                            icon: Icons.confirmation_number,
                            label: 'REQUESTED'),
                        const SizedBox(height: 14),
                        kText(booking['filmName'] ?? 'Film screening',
                            size: 24, weight: FontWeight.w900),
                        const SizedBox(height: 18),
                        _row('Seats', seats.isEmpty ? '—' : seats.join(', ')),
                        _row('Total', '${booking['total'] ?? 0} TND'),
                        if (show.isNotEmpty) ...[
                          _row('Date', show.first.date),
                          _row('Time', '${show.first.hd} – ${show.first.hf}'),
                          _row('Room', show.first.room),
                        ],
                      ]),
                ),
              ),
              const SizedBox(height: 18),
              kText(
                  'This is a seat request, not a paid ticket. Seats are not held until the cinema confirms.',
                  size: 12,
                  color: muted),
            ]),
          );
        },
      ),
    );
  }

  Widget _row(String label, String value) => Padding(
        padding: const EdgeInsets.symmetric(vertical: 7),
        child:
            Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
          kText(label, size: 12, color: muted),
          kText(value, size: 13, weight: FontWeight.w800),
        ]),
      );
}

class TicketsTab extends StatelessWidget {
  const TicketsTab();
  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
            title: kText('My tickets', size: 22, weight: FontWeight.w900),
            actions: [profileAvatar()]),
        body: StreamBuilder(
          stream: watchReservations(FirebaseAuth.instance.currentUser!.uid),
          builder: (_, snap) {
            final bookings = snap.data ?? [];
            if (snap.hasError)
              return Center(
                  child: kText('Could not load seat requests.', color: muted));
            if (!snap.hasData)
              return const Center(child: CircularProgressIndicator());
            if (bookings.isEmpty)
              return Center(child: kText('No seat requests yet', color: muted));
            return ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: bookings.length,
              itemBuilder: (_, i) {
                final b = bookings[i];
                final seats = List<String>.from(b['seats'] ?? const []);
                return Card(
                    child: ListTile(
                  leading: const Icon(Icons.confirmation_number, color: redHot),
                  title: kText(b['filmName'] ?? 'Film screening',
                      size: 13, weight: FontWeight.w800),
                  subtitle: kText(
                      '${seats.join(', ')} · ${b['total'] ?? 0} TND · Requested',
                      size: 11,
                      color: muted),
                  trailing: const Icon(Icons.arrow_forward_ios,
                      size: 14, color: muted),
                  onTap: () => Navigator.of(context).push(MaterialPageRoute(
                      builder: (_) => TicketDetailScreen(booking: b))),
                ));
              },
            );
          },
        ),
      );
}

class ShopTab extends StatefulWidget {
  const ShopTab();

  @override
  State<ShopTab> createState() => ShopTabState();
}

class ShopTabState extends State<ShopTab> {
  int _view = 0; // 0 = catalogue, 1 = basket
  final uid = FirebaseAuth.instance.currentUser!.uid;

  @override
  Widget build(BuildContext context) => PopScope(
        canPop: _view == 0,
        onPopInvokedWithResult: (didPop, result) {
          if (!didPop && _view == 1) setState(() => _view = 0);
        },
        child: Scaffold(
          appBar: AppBar(
            title: kText(_view == 0 ? 'Cinémathèque' : 'Your basket',
                size: 20, weight: FontWeight.w900),
            actions: [
              IconButton(
                tooltip: _view == 0 ? 'Open basket' : 'Back to shop',
                icon: Icon(_view == 0
                    ? Icons.shopping_bag_outlined
                    : Icons.storefront_outlined),
                onPressed: () => setState(() => _view = _view == 0 ? 1 : 0),
              ),
              profileAvatar(),
            ],
          ),
          body: _view == 0 ? _productCatalogue() : _basketView(),
        ),
      );

  Widget _productCatalogue() => StreamBuilder(
        stream: watchProducts(),
        builder: (context, snap) {
          final products = snap.data ?? [];
          if (!snap.hasData) {
            return const Center(child: CircularProgressIndicator(color: red));
          }
          return StreamBuilder(
            stream: watchCart(uid),
            builder: (context, cartSnap) {
              final items = cartSnap.data ?? [];
              final count = items.fold<int>(
                  0,
                  (sum, item) =>
                      sum + ((item['quantity'] as num?)?.toInt() ?? 0));
              final total = items.fold<int>(
                  0,
                  (sum, item) =>
                      sum +
                      ((item['price'] as num?)?.toInt() ?? 0) *
                          ((item['quantity'] as num?)?.toInt() ?? 1));
              return SingleChildScrollView(
                padding: const EdgeInsets.only(bottom: 28),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Padding(
                      padding: const EdgeInsets.fromLTRB(20, 16, 20, 0),
                      child: _Banner(
                        image: products.isNotEmpty
                            ? products.first.image
                            : 'assets/images/product-sculpture.jpg',
                        height: 190,
                        overlay: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.end,
                            children: [
                              const _Pill(
                                  icon: Icons.shopping_bag,
                                  label: 'THE CINÉMATHÈQUE'),
                              const SizedBox(height: 8),
                              kText('Take the cinema home',
                                  size: 22, weight: FontWeight.w900),
                              const SizedBox(height: 4),
                              kText('Collectibles and treats for movie nights',
                                  size: 12, color: Colors.white70),
                            ]),
                      ),
                    ),
                    const SizedBox(height: 26),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 20),
                      child: kSectionHead(
                          'Curated for you', '${products.length} products',
                          trailing:
                              const Icon(Icons.shopping_bag, color: redHot)),
                    ),
                    const SizedBox(height: 12),
                    GridView.builder(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      padding: const EdgeInsets.symmetric(horizontal: 20),
                      gridDelegate:
                          const SliverGridDelegateWithFixedCrossAxisCount(
                        crossAxisCount: 2,
                        mainAxisSpacing: 14,
                        crossAxisSpacing: 14,
                        childAspectRatio: .64,
                      ),
                      itemCount: products.length,
                      itemBuilder: (_, i) {
                        final product = products[i];
                        return GestureDetector(
                          onTap: () => Navigator.of(context).push(
                              MaterialPageRoute(
                                  builder: (_) => ProductDetailScreen(
                                      product: product, catalogue: products))),
                          child: Card(
                            clipBehavior: Clip.antiAlias,
                            child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Expanded(
                                    child:
                                        Stack(fit: StackFit.expand, children: [
                                      Image.asset(product.image,
                                          fit: BoxFit.cover,
                                          errorBuilder: (_, __, ___) =>
                                              _fallback(Icons.shopping_bag)),
                                      Positioned(
                                        top: 8,
                                        right: 8,
                                        child: Container(
                                          padding: const EdgeInsets.symmetric(
                                              horizontal: 8, vertical: 4),
                                          decoration: BoxDecoration(
                                            color: red,
                                            borderRadius:
                                                BorderRadius.circular(12),
                                          ),
                                          child: kText('${product.price} TND',
                                              size: 10,
                                              color: Colors.white,
                                              weight: FontWeight.w800),
                                        ),
                                      ),
                                    ]),
                                  ),
                                  Padding(
                                    padding: const EdgeInsets.all(10),
                                    child: Column(
                                        crossAxisAlignment:
                                            CrossAxisAlignment.start,
                                        children: [
                                          kText(product.name,
                                              size: 12,
                                              weight: FontWeight.w800,
                                              maxLines: 2),
                                          const SizedBox(height: 2),
                                          kText(product.description,
                                              size: 10,
                                              color: muted,
                                              maxLines: 2),
                                          const SizedBox(height: 8),
                                          SizedBox(
                                            width: double.infinity,
                                            child: kButton('ADD', () async {
                                              await addToCart(uid, product);
                                              if (context.mounted) {
                                                ScaffoldMessenger.of(context)
                                                    .showSnackBar(SnackBar(
                                                        content: Text(
                                                            '${product.name} added to your basket')));
                                              }
                                            }, icon: Icons.add_shopping_cart),
                                          ),
                                        ]),
                                  ),
                                ]),
                          ),
                        );
                      },
                    ),
                    if (count > 0)
                      Padding(
                        padding: const EdgeInsets.fromLTRB(20, 18, 20, 0),
                        child: Card(
                          child: ListTile(
                            leading:
                                const Icon(Icons.shopping_bag, color: redHot),
                            title: kText('$count item(s) in your basket',
                                size: 13, weight: FontWeight.w800),
                            subtitle:
                                kText('$total TND', size: 12, color: gold),
                            trailing: const Icon(Icons.arrow_forward_ios,
                                size: 16, color: muted),
                            onTap: () => setState(() => _view = 1),
                          ),
                        ),
                      ),
                  ],
                ),
              );
            },
          );
        },
      );

  Widget _basketView() => StreamBuilder(
        stream: watchCart(uid),
        builder: (context, snap) {
          final items = snap.data ?? [];
          final total = items.fold<int>(
              0,
              (sum, item) =>
                  sum +
                  ((item['price'] as num?)?.toInt() ?? 0) *
                      ((item['quantity'] as num?)?.toInt() ?? 1));
          if (!snap.hasData) {
            return const Center(child: CircularProgressIndicator(color: red));
          }
          if (items.isEmpty) {
            return Center(
                child: Column(mainAxisSize: MainAxisSize.min, children: [
              const Icon(Icons.shopping_bag_outlined, size: 64, color: muted),
              const SizedBox(height: 16),
              kText('Your basket is empty', size: 17, color: muted),
              const SizedBox(height: 18),
              kPrimaryButton(
                  'BROWSE THE SHOP', () => setState(() => _view = 0)),
            ]));
          }
          return Column(children: [
            Expanded(
                child: ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: items.length,
              itemBuilder: (_, i) {
                final item = items[i];
                final qty = (item['quantity'] as num?)?.toInt() ?? 1;
                return Card(
                    child: Padding(
                  padding: const EdgeInsets.all(10),
                  child: Row(children: [
                    ClipRRect(
                      borderRadius: BorderRadius.circular(10),
                      child: SizedBox(
                        width: 64,
                        height: 64,
                        child: Image.asset(item['image'] ?? '',
                            fit: BoxFit.cover,
                            errorBuilder: (_, __, ___) =>
                                _fallback(Icons.shopping_bag)),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                        child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                          kText(item['name'] ?? '',
                              size: 13, weight: FontWeight.w800),
                          const SizedBox(height: 4),
                          kText('${item['price'] ?? 0} TND each',
                              size: 11, color: gold),
                        ])),
                    Row(mainAxisSize: MainAxisSize.min, children: [
                      IconButton(
                          tooltip: 'Decrease quantity',
                          icon: const Icon(Icons.remove_circle_outline),
                          onPressed: () => qty > 1
                              ? setCartQuantity(uid, item['id'], qty - 1)
                              : removeFromCart(uid, item['id'])),
                      kText('$qty', size: 13, weight: FontWeight.w800),
                      IconButton(
                          tooltip: 'Increase quantity',
                          icon: const Icon(Icons.add_circle_outline),
                          onPressed: () =>
                              setCartQuantity(uid, item['id'], qty + 1)),
                    ]),
                  ]),
                ));
              },
            )),
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                  color: surface, border: Border(top: BorderSide(color: line))),
              child: Row(children: [
                Expanded(
                    child: kText('Total: $total TND',
                        size: 15, weight: FontWeight.w800)),
                kButton(
                    'SAVE BASKET',
                    () => ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(
                            content: Text(
                                'Your basket is saved to your account.')))),
              ]),
            ),
          ]);
        },
      );
}

class SeriesTab extends StatelessWidget {
  const SeriesTab();

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
          title: kText('Series', size: 22, weight: FontWeight.w900),
          actions: [profileAvatar()],
        ),
        body: StreamBuilder(
          stream: watchSeries(),
          builder: (context, snap) {
            final series = snap.data ?? [];
            if (!snap.hasData) {
              return const Center(child: CircularProgressIndicator(color: red));
            }
            if (series.isEmpty) {
              return Center(child: kText('No series yet', color: muted));
            }
            final featured = series.first;
            return SingleChildScrollView(
              padding: const EdgeInsets.only(bottom: 28),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Padding(
                    padding: const EdgeInsets.fromLTRB(20, 16, 20, 0),
                    child: _Banner(
                      image: featured.image,
                      height: 220,
                      overlay: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.end,
                          children: [
                            const _Pill(
                                icon: Icons.auto_awesome,
                                label: 'FEATURED SERIES'),
                            const SizedBox(height: 8),
                            kText(featured.name,
                                size: 23, weight: FontWeight.w900, maxLines: 2),
                            const SizedBox(height: 4),
                            kText(featured.director,
                                size: 12, color: Colors.white70),
                          ]),
                    ),
                  ),
                  const SizedBox(height: 26),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    child: kSectionHead(
                        'Binge worthy', '${series.length} series',
                        trailing:
                            const Icon(Icons.video_collection, color: redHot)),
                  ),
                  const SizedBox(height: 12),
                  GridView.builder(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    gridDelegate:
                        const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 2,
                      mainAxisSpacing: 14,
                      crossAxisSpacing: 14,
                      childAspectRatio: .66,
                    ),
                    itemCount: series.length,
                    itemBuilder: (_, i) {
                      final item = series[i];
                      return GestureDetector(
                        onTap: () => Navigator.of(context).push(
                            MaterialPageRoute(
                                builder: (_) => SeriesDetailScreen(
                                    series: item, catalogue: series))),
                        child: Card(
                          clipBehavior: Clip.antiAlias,
                          child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Expanded(
                                    child:
                                        Stack(fit: StackFit.expand, children: [
                                  Image.asset(item.image,
                                      fit: BoxFit.cover,
                                      errorBuilder: (_, __, ___) =>
                                          _fallback(Icons.tv)),
                                  const DecoratedBox(
                                    decoration: BoxDecoration(
                                      gradient: LinearGradient(
                                        begin: Alignment.topCenter,
                                        end: Alignment.bottomCenter,
                                        colors: [
                                          Colors.transparent,
                                          Color(0xBB0B0709)
                                        ],
                                        stops: [0.45, 1],
                                      ),
                                    ),
                                  ),
                                  Positioned(
                                    left: 10,
                                    right: 10,
                                    bottom: 10,
                                    child: kText(item.name,
                                        size: 15,
                                        weight: FontWeight.w900,
                                        maxLines: 2),
                                  ),
                                ])),
                                Padding(
                                  padding: const EdgeInsets.all(10),
                                  child: Column(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.start,
                                      children: [
                                        Row(children: [
                                          const Icon(Icons.person_outline,
                                              size: 11, color: gold),
                                          const SizedBox(width: 4),
                                          Expanded(
                                              child: kText(item.director,
                                                  size: 10,
                                                  color: gold,
                                                  maxLines: 1)),
                                        ]),
                                        const SizedBox(height: 5),
                                        kText(item.summary,
                                            size: 10,
                                            color: muted,
                                            maxLines: 3),
                                      ]),
                                ),
                              ]),
                        ),
                      );
                    },
                  ),
                ],
              ),
            );
          },
        ),
      );
}

class ProfilePage extends StatelessWidget {
  const ProfilePage();

  @override
  Widget build(BuildContext context) {
    final user = FirebaseAuth.instance.currentUser!;
    return Scaffold(
      appBar:
          AppBar(title: kText('Profile', size: 22, weight: FontWeight.w900)),
      body: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(children: [
            const CircleAvatar(
                radius: 42,
                backgroundColor: surface2,
                child: Icon(Icons.person, size: 40, color: redHot)),
            const SizedBox(height: 16),
            kText(user.displayName ?? 'Rakcha member',
                size: 20, weight: FontWeight.w800),
            kText(user.email ?? '', size: 12, color: muted),
            const SizedBox(height: 24),
            StreamBuilder(
              stream: watchReservations(user.uid),
              builder: (_, snapshot) => kKpi(
                  snapshot.data?.length.toString() ?? '—',
                  'Seat requests',
                  Icons.confirmation_number),
            ),
            const SizedBox(height: 24),
            SizedBox(
                width: double.infinity,
                child: kOutlineButton(
                    'SIGN OUT', () => FirebaseAuth.instance.signOut())),
          ])),
    );
  }
}
