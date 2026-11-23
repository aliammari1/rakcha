// Responsive navigation
for (const button of document.querySelectorAll('[data-rk-menu]')) {
    button.addEventListener('click', () => {
        const mobile = document.querySelector('.rk-mobile-menu');
        const workspace = document.getElementById('rakcha-workspace-nav');
        if (mobile) {
            const open = mobile.classList.toggle('is-open');
            button.setAttribute('aria-expanded', String(open));
        }
        if (workspace) workspace.classList.toggle('is-open');
    });
}
document.addEventListener('keydown', (event) => {
    if (event.key !== 'Escape') return;
    document.querySelector('.rk-mobile-menu')?.classList.remove('is-open');
    document.getElementById('rakcha-workspace-nav')?.classList.remove('is-open');
});
document.addEventListener('click', (event) => {
    if (event.target.closest('.rk-mobile-menu a') && matchMedia('(max-width: 820px)').matches) {
        document.querySelector('.rk-mobile-menu')?.classList.remove('is-open');
    }
    if (event.target.closest('.rk-ws-nav a') && matchMedia('(max-width: 820px)').matches) {
        document.getElementById('rakcha-workspace-nav')?.classList.remove('is-open');
    }
});

// Film filter chips
for (const button of document.querySelectorAll('[data-rk-filter]')) {
    button.addEventListener('click', () => {
        const mode = button.dataset.rkFilter;
        for (const sibling of button.parentElement.querySelectorAll('[data-rk-filter]')) {
            sibling.classList.toggle('is-active', sibling === button);
        }
        for (const film of document.querySelectorAll('[data-rk-films] .rk-film')) {
            film.hidden = mode === 'now' && film.dataset.hasShowtime !== 'true';
        }
    });
}

// Showtime date tabs + quick date filters
for (const agenda of document.querySelectorAll('[data-agenda]')) {
    const links = [...agenda.querySelectorAll('[data-agenda-day]')];
    const panels = [...agenda.querySelectorAll('[data-agenda-panel]')];

    function select(id) {
        for (const panel of panels) panel.hidden = `#${panel.id}` !== id;
        for (const link of links) {
            if (link.hash === id) link.setAttribute('aria-current', 'date');
            else link.removeAttribute('aria-current');
        }
    }

    select(links.find(link => link.hash === location.hash)?.hash ?? links[0].hash);
    for (const link of links) {
        link.addEventListener('click', event => {
            if (event.ctrlKey || event.metaKey || event.shiftKey || event.altKey) return;
            event.preventDefault();
            select(link.hash);
            history.replaceState(null, '', link.hash);
        });
    }
}

for (const group of document.querySelectorAll('[data-rk-quickdates]')) {
    const buttons = [...group.querySelectorAll('[data-offset]')];
    const agenda = document.querySelector('[data-agenda]');
    if (!agenda) continue;
    for (const button of buttons) {
        button.addEventListener('click', () => {
            const offset = Number(button.dataset.offset);
            const day = new Date();
            day.setDate(day.getDate() + offset);
            const id = `date-${day.toISOString().slice(0, 10)}`;
            const link = agenda.querySelector(`[data-agenda-day][href="#${id}"]`);
            if (!link) return;
            for (const other of buttons) other.classList.toggle('is-active', other === button);
            link.click();
            link.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'center' });
        });
    }
}

if (!matchMedia('(prefers-reduced-motion: reduce)').matches && 'IntersectionObserver' in window) {
    const observer = new IntersectionObserver(entries => {
        for (const entry of entries) {
            if (entry.isIntersecting) {
                entry.target.animate(
                    [{ opacity: 0.5, transform: 'translateY(8px)' }, { opacity: 1, transform: 'translateY(0)' }],
                    { duration: 320, easing: 'cubic-bezier(.2,.8,.2,1)' },
                );
                observer.unobserve(entry.target);
            }
        }
    }, { threshold: 0.08 });
    for (const item of document.querySelectorAll('.rk-film, .rk-kpi, .rk-hero__body')) observer.observe(item);
}
