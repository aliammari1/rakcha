"use strict";
/**
 * @description Sets the `PerfectScrollbar` function active for specific elements on
 * a Windows OS based on their class names.
 */
(function() {
  var isWindows = navigator.platform.indexOf('Win') > -1 ? true : false;

  if (isWindows) {
    // if we are on windows OS we activate the perfectScrollbar function
    if (document.getElementsByClassName('main-content')[0]) {
      var mainpanel = document.querySelector('.main-content');
      var ps = new PerfectScrollbar(mainpanel);
    }

    if (document.getElementsByClassName('sidenav')[0]) {
      var sidebar = document.querySelector('.sidenav');
      var ps1 = new PerfectScrollbar(sidebar);
    }

    if (document.getElementsByClassName('navbar-collapse')[0]) {
      var fixedplugin = document.querySelector('.navbar:not(.navbar-expand-lg) .navbar-collapse');
      var ps2 = new PerfectScrollbar(fixedplugin);
    }

    if (document.getElementsByClassName('fixed-plugin')[0]) {
      var fixedplugin = document.querySelector('.fixed-plugin');
      var ps3 = new PerfectScrollbar(fixedplugin);
    }
  }
})();

// Verify navbar blur on scroll
if (document.getElementById('navbarBlur')) {
  navbarBlurOnScroll('navbarBlur');
}

// initialization of Tooltips
var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
/**
 * @description Creates a `Bootstrap.Tooltip` instance for an element passed to it,
 * returning the instance.
 * 
 * @param { HTML Element (more specifically, an element of type 'button', 'a', or
 * 'div'). } tooltipTriggerEl - element that will trigger the tooltip display when
 * the mouse pointer is hovered over it.
 * 
 * 		- `tooltipTriggerEl`: The input parameter, an HTMLElement object, which represents
 * the trigger element for the tooltip.
 * 
 * @returns { bootstrap.Tooltip` instance } a new `bootstrap.Tooltip` instance
 * initialized with the provided element.
 * 
 * 		- `tooltipTriggerEl`: The element that triggers the tooltip to appear.
 * 
 * 	Overall, this function creates a new instance of Bootstrap's Tooltip component
 * and passes in the trigger element as an argument, returning the generated tooltip
 * object.
 */
var tooltipList = tooltipTriggerList.map(function(tooltipTriggerEl) {
  return new bootstrap.Tooltip(tooltipTriggerEl)
})


// when input is focused add focused class for style
/**
 * @description Adds the `focused` class to an element's parent element if it has the
 * `input-group` class.
 * 
 * @param { object } el - element to which the focused class will be added if its
 * parent element contains the class `input-group`.
 */
function focused(el) {
  if (el.parentElement.classList.contains('input-group')) {
    el.parentElement.classList.add('focused');
  }
}

// when input is focused remove focused class for style
/**
 * @description Removes the `focused` class from a direct parent element of an element
 * if the direct parent has the `input-group` class.
 * 
 * @param { HTML Element } el - element whose class will be removed if it has been
 * applied, based on the condition of the parent element's class list.
 * 
 * 		- `el`: A reference to an HTML element in the current document.
 * 		- `parentElement`: The parent element of the input element, which is also an
 * HTML element.
 */
function defocused(el) {
  if (el.parentElement.classList.contains('input-group')) {
    el.parentElement.classList.remove('focused');
  }
}

// helper for adding on all elements multiple attributes
/**
 * @description Sets attributes on an HTML element based on options provided in an object.
 * 
 * @param { element. } el - HTML element whose attributes are to be set according to
 * the provided options.
 * 
 * 		- `el`: The HTML element for which attribute values will be set. It can have any
 * number of properties or attributes.
 * 
 * @param { object } options - attribute settings to be applied to the specified
 * element (el) in the form of an object, with each key representing an attribute
 * name and the corresponding value representing the attribute value to be set.
 */
function setAttributes(el, options) {
  Object.keys(options).forEach(function(attr) {
    el.setAttribute(attr, options[attr]);
  })
}

// adding on inputs attributes for calling the focused and defocused functions
if (document.querySelectorAll('.input-group').length != 0) {
  var allInputs = document.querySelectorAll('input.form-control');
  allInputs.forEach(el => setAttributes(el, {
    "onfocus": "focused(this)",
    "onfocusout": "defocused(this)"
  }));
}

// Fixed Plugin
if (document.querySelector('.fixed-plugin')) {
  var fixedPlugin = document.querySelector('.fixed-plugin');
  var fixedPluginButton = document.querySelector('.fixed-plugin-button');
  var fixedPluginButtonNav = document.querySelector('.fixed-plugin-button-nav');
  var fixedPluginCard = document.querySelector('.fixed-plugin .card');
  var fixedPluginCloseButton = document.querySelectorAll('.fixed-plugin-close-button');
  var navbar = document.getElementById('navbarBlur');
  var buttonNavbarFixed = document.getElementById('navbarFixed');

  if (fixedPluginButton) {
    /**
     * @description Determines if `fixedPlugin` has the class "show" added or removed
     * based on its current class list.
     */
    fixedPluginButton.onclick = function() {
      if (!fixedPlugin.classList.contains('show')) {
        fixedPlugin.classList.add('show');
      } else {
        fixedPlugin.classList.remove('show');
      }
    }
  }

  if (fixedPluginButtonNav) {
    /**
     * @description Toggles the `show` class on or off for a element with the ID `fixedPlugin`.
     */
    fixedPluginButtonNav.onclick = function() {
      if (!fixedPlugin.classList.contains('show')) {
        fixedPlugin.classList.add('show');
      } else {
        fixedPlugin.classList.remove('show');
      }
    }
  }

  /**
   * @description Removes `fixedPlugin` class when element is clicked.
   * 
   * @param { element (a DOM node) of HTMLElement class type. } el - element being
   * targeted by the event handler, and is used to apply the event listener to that
   * specific element.
   * 
   * 		- `onclick`: an event handler that removes the class "show" from a DOM element
   * (the exact object is not specified) when it is clicked.
   */
  fixedPluginCloseButton.forEach(function(el) {
    /**
     * @description Removes the 'show' class from a specific element denoted by `fixedPlugin`.
     */
    el.onclick = function() {
      fixedPlugin.classList.remove('show');
    }
  })

  /**
   * @description Detects if a particular event is happening on a page and removes a
   * CSS class from an element if it's not a specific button or card component.
   * 
   * @param { Event. } e - Event object that triggered the function, and it is used to
   * determine whether the show class should be removed from an element.
   * 
   * 		- `target`: This refers to the DOM element that triggered the event. It can be
   * either a `HTMLButtonElement`, `HTMLInputElement`, or other types of elements.
   * 		- `fixedPluginButton`: This is a boolean property that indicates whether the
   * event was triggered by a fixed plugin button.
   * 		- `fixedPluginButtonNav`: This is another boolean property that indicates whether
   * the event was triggered by a fixed plugin button within a navigation context (i.e.,
   * within a navigation bar).
   * 		- `closest`.`fixed-plugin `.`card`: This refers to the closest ancestor element
   * of type `<div>` with a class of `fixed-plugin` and a class of `card`. This property
   * can be used to determine if the event was triggered by a card element within a
   * fixed plugin context.
   */
  document.querySelector('body').onclick = function(e) {
    if (e.target != fixedPluginButton && e.target != fixedPluginButtonNav && e.target.closest('.fixed-plugin .card') != fixedPluginCard) {
      fixedPlugin.classList.remove('show');
    }
  }

  if (navbar) {
    if (navbar.getAttribute('data-scroll') == 'true' && buttonNavbarFixed) {
      buttonNavbarFixed.setAttribute("checked", "true");
    }
  }

}

//Set Sidebar Color
/**
 * @description Modifies the class attribute of an HTML element with the attribute
 * `data-color` based on its value, and sets the background color gradient of a parent
 * element.
 * 
 * @param { element or reference of HTMLElement } a - element that is being modified
 * by the sidebarColor() function.
 * 
 * 		- `parent`: An array of elements descendant to `a`.
 * 		- `color`: The `data-color` attribute value of `a`.
 * 		- `classList`: The class list of `a`, which can be modified through the function.
 * 
 * 	The function processes the properties of `a` as follows:
 * 
 * 	1/ Removes any existing "active" class from elements in the `parent` array.
 * 	2/ Adds or removes the "active" class from `a` based on its `data-color` attribute
 * value and the current value of `color`.
 * 	3/ Sets the `data-color` attribute value of the `.sidenav` element to match the
 * `color` value.
 * 	4/ If the `#sidenavCard` element exists, adds or removes classes from it based
 * on the `color` value.
 */
function sidebarColor(a) {
  var parent = a.parentElement.children;
  var color = a.getAttribute("data-color");

  for (var i = 0; i < parent.length; i++) {
    parent[i].classList.remove('active');
  }

  if (!a.classList.contains('active')) {
    a.classList.add('active');
  } else {
    a.classList.remove('active');
  }

  var sidebar = document.querySelector('.sidenav');
  sidebar.setAttribute("data-color", color);

  if (document.querySelector('#sidenavCard')) {
    var sidenavCard = document.querySelector('#sidenavCard+.btn+.btn');
    let sidenavCardClasses = ['btn', 'btn-sm', 'w-100', 'mb-0', 'bg-gradient-' + color];
    sidenavCard.removeAttribute('class');
    sidenavCard.classList.add(...sidenavCardClasses);
  }
}

// Set Sidebar Type
/**
 * @description Modifies the class of elements related to the sidebar based on the
 * provided color attribute value. It updates the background and text colors of the
 * sidebar, removes unwanted classes, and sets new classes for the logo and branding.
 * 
 * @param { element. } a - element being processed, which is passed through a series
 * of operations to update its styles and remove any unnecessary class names.
 * 
 * 		- `parent`: The parent element of `a`.
 * 		- `children`: An array of child elements inside `parent`.
 * 		- `getAttribute('data-class')`: Returns a string value of `color`, which is an
 * attribute on `a`.
 * 		- `querySelector`: A function that retrieves the first element matching a given
 * selector in the DOM. In this case, it retrieves the `body` element and its descendants.
 * 		- `document`: The root element of the document.
 * 		- `querySelectorAll`: A function that retrieves multiple elements matching a
 * given selector in the DOM. In this case, it retrieves all elements with class
 * `text-white` or `text-dark` inside the `body` element and its descendants.
 * 		- `classList`: The collection of classes applied to an element. In this case,
 * it is used to add/remove classes from `a`.
 * 		- `remove`: Removes a class from an element.
 * 		- `add`: Adds a class to an element.
 * 		- `getAttribute`: Returns the value of an attribute on an element. In this case,
 * it returns the `data-class` attribute value of `a`.
 * 		- `classList.contains()`: A method that checks if a given class is present in
 * an element's class list.
 * 		- `for`: A loop iterating over an array.
 * 		- `i`: An iteration variable for the loop.
 * 		- `parent[i]`: The i-th child element of `parent`.
 */
function sidebarType(a) {
  var parent = a.parentElement.children;
  var color = a.getAttribute("data-class");
  var body = document.querySelector("body");
  var bodyWhite = document.querySelector("body:not(.dark-version)");
  var bodyDark = body.classList.contains('dark-version');

  var colors = [];

  for (var i = 0; i < parent.length; i++) {
    parent[i].classList.remove('active');
    colors.push(parent[i].getAttribute('data-class'));
  }

  if (!a.classList.contains('active')) {
    a.classList.add('active');
  } else {
    a.classList.remove('active');
  }

  var sidebar = document.querySelector('.sidenav');

  for (var i = 0; i < colors.length; i++) {
    sidebar.classList.remove(colors[i]);
  }

  sidebar.classList.add(color);


  // Remove text-white/text-dark classes
  if (color == 'bg-white') {
    var textWhites = document.querySelectorAll('.sidenav .text-white');
    for (let i = 0; i < textWhites.length; i++) {
      textWhites[i].classList.remove('text-white');
      textWhites[i].classList.add('text-dark');
    }
  } else {
    var textDarks = document.querySelectorAll('.sidenav .text-dark');
    for (let i = 0; i < textDarks.length; i++) {
      textDarks[i].classList.add('text-white');
      textDarks[i].classList.remove('text-dark');
    }
  }

  if (color == 'bg-default' && bodyDark) {
    var textDarks = document.querySelectorAll('.navbar-brand .text-dark');
    for (let i = 0; i < textDarks.length; i++) {
      textDarks[i].classList.add('text-white');
      textDarks[i].classList.remove('text-dark');
    }
  }

  // Remove logo-white/logo-dark

  if ((color == 'bg-white') && bodyWhite) {
    var navbarBrand = document.querySelector('.navbar-brand-img');
    var navbarBrandImg = navbarBrand.src;

    if (navbarBrandImg.includes('logo-ct.png')) {
      var navbarBrandImgNew = navbarBrandImg.replace("logo-ct", "logo-ct-dark");
      navbarBrand.src = navbarBrandImgNew;
    }
  } else {
    var navbarBrand = document.querySelector('.navbar-brand-img');
    var navbarBrandImg = navbarBrand.src;
    if (navbarBrandImg.includes('logo-ct-dark.png')) {
      var navbarBrandImgNew = navbarBrandImg.replace("logo-ct-dark", "logo-ct");
      navbarBrand.src = navbarBrandImgNew;
    }
  }

  if (color == 'bg-white' && bodyDark) {
    var navbarBrand = document.querySelector('.navbar-brand-img');
    var navbarBrandImg = navbarBrand.src;

    if (navbarBrandImg.includes('logo-ct.png')) {
      var navbarBrandImgNew = navbarBrandImg.replace("logo-ct", "logo-ct-dark");
      navbarBrand.src = navbarBrandImgNew;
    }
  }
}

// Set Navbar Fixed
/**
 * @description Modifies the classes and attributes of an HTML element with ID
 * 'navbarBlur' based on whether a checkbox is checked.
 * 
 * @param { "HTMLInputElement" or "Element". } el - element whose fixed navigation
 * behavior is being controlled, and it is used to toggle the attribute `checked` on
 * the element to determine whether the navigation links should be displayed in a
 * blurred state or not.
 * 
 * 		- `el`: An HTML Element with a checked attribute that triggers the function. The
 * element can be any type (button, input, div, etc.) but must have the checked
 * attribute set to true or false.
 */
function navbarFixed(el) {
  let classes = ['position-sticky', 'bg-white', 'left-auto', 'top-2', 'z-index-sticky'];
  const navbar = document.getElementById('navbarBlur');

  if (!el.getAttribute("checked")) {
    toggleNavLinksColor('blur');
    navbar.classList.add(...classes);
    navbar.setAttribute('data-scroll', 'true');
    navbarBlurOnScroll('navbarBlur');
    el.setAttribute("checked", "true");
  } else {
    toggleNavLinksColor('transparent');
    navbar.classList.remove(...classes);
    navbar.setAttribute('data-scroll', 'false');
    navbarBlurOnScroll('navbarBlur');
    el.removeAttribute("checked");
  }
}

// Set Navbar Minimized
/**
 * @description Minimizes or pins a side navigation (sidenav) based on an attribute
 * in the HTML element with the class `g-sidenav-show`.
 * 
 * @param { `HTMLInputElement`. } el - element that triggers the sidenav to minimize
 * or expand, and is used to toggle the sidenav's pinned or hidden state when its
 * attribute is changed.
 * 
 * 		- `el`: The element to be minimized. It has an `checked` attribute that controls
 * whether the side navigation is pinned or hidden.
 */
function navbarMinimize(el) {
  var sidenavShow = document.getElementsByClassName('g-sidenav-show')[0];

  if (!el.getAttribute("checked")) {
    sidenavShow.classList.remove('g-sidenav-pinned');
    sidenavShow.classList.add('g-sidenav-hidden');
    el.setAttribute("checked", "true");
  } else {
    sidenavShow.classList.remove('g-sidenav-hidden');
    sidenavShow.classList.add('g-sidenav-pinned');
    el.removeAttribute("checked");
  }
}

/**
 * @description Updates the color and background of navigation links and their togglers
 * based on user input.
 * 
 * @param { string } type - state of the links' colors to be toggled, with possible
 * values being "blur" or "transparent".
 */
function toggleNavLinksColor(type) {
  let navLinks = document.querySelectorAll('.navbar-main .nav-link, .navbar-main .breadcrumb-item, .navbar-main .breadcrumb-item a, .navbar-main h6')
  let navLinksToggler = document.querySelectorAll('.navbar-main .sidenav-toggler-line')

  if (type === "blur") {
    navLinks.forEach(element => {
      element.classList.remove('text-white')
    });

    navLinksToggler.forEach(element => {
      element.classList.add('bg-dark')
      element.classList.remove('bg-white')
    });
  } else if (type === "transparent") {
    navLinks.forEach(element => {
      element.classList.add('text-white')
    });

    navLinksToggler.forEach(element => {
      element.classList.remove('bg-dark')
      element.classList.add('bg-white')
    });
  }
}


// Navbar blur on scroll
/**
 * @description Monitors scroll distance and sets a sticky navbar on iOS devices. It
 * debounces the scroll event to ensure only one event is processed per scroll. The
 * navbar class changes depending on whether scroll distance is above or below a set
 * value.
 * 
 * @param { string } id - id of the navbar element to which the scrolling functionality
 * should be applied.
 */
function navbarBlurOnScroll(id) {
  const navbar = document.getElementById(id);
  let navbarScrollActive = navbar ? navbar.getAttribute("data-scroll") : false;
  let scrollDistance = 5;
  let classes = ['bg-white', 'left-auto', 'position-sticky'];
  let toggleClasses = ['shadow-none'];

  if (navbarScrollActive == 'true') {
    window.onscroll = debounce(function() {
      if (window.scrollY > scrollDistance) {
        blurNavbar();
      } else {
        transparentNavbar();
      }
    }, 10);
  } else {
    window.onscroll = debounce(function() {
      transparentNavbar();
    }, 10);
  }

  var isWindows = navigator.platform.indexOf('Win') > -1 ? true : false;

  if (isWindows) {
    var content = document.querySelector('.main-content');
    if (navbarScrollActive == 'true') {
      content.addEventListener('ps-scroll-y', debounce(function() {
        if (content.scrollTop > scrollDistance) {
          blurNavbar();
        } else {
          transparentNavbar();
        }
      }, 10));
    } else {
      content.addEventListener('ps-scroll-y', debounce(function() {
        transparentNavbar();
      }, 10));
    }
  }

  /**
   * @description Adds or removes class(es) to a navbar and toggles the color of
   * navigational links using a specified list of classes and toggleClasses, respectively.
   */
  function blurNavbar() {
    navbar.classList.add(...classes)
    navbar.classList.remove(...toggleClasses)

    toggleNavLinksColor('blur');
  }

  /**
   * @description Updates the class list of a `<nav>` element named `navbar`, removing
   * some classes and adding new ones, and also changes the color of links inside it
   * to "transparent".
   */
  function transparentNavbar() {
    navbar.classList.remove(...classes)
    navbar.classList.add(...toggleClasses)

    toggleNavLinksColor('transparent');
  }
}


// Debounce Function
// Returns a function, that, as long as it continues to be invoked, will not
// be triggered. The function will be called after it stops being called for
// N milliseconds. If `immediate` is passed, trigger the function on the
// leading edge, instead of the trailing.
/**
 * @description Wraps a given function with a timeout, delaying its execution after
 * a specified wait time has passed. It then repeatedly calls the function at regular
 * intervals until the `immediate` parameter is set to `true`.
 * 
 * @param { function. } func - function to be debounced and is used to apply it to
 * the context and arguments when the debounce function is called.
 * 
 * 		- `func`: The function to debounce. This is the primary argument passed to the
 * function.
 * 		- `wait`: The time to wait before executing the original function after a debounce
 * event has occurred. It can be a number or a promise that resolves with a number
 * representing the debounce time in milliseconds.
 * 
 * @param { number } wait - duration of the delay before executing the passed function
 * after the first invocation.
 * 
 * @param { boolean } immediate - ability to call the passed function without delay,
 * immediately after it is invoked.
 * 
 * @returns { anonymous function } a function that debounces a provided function after
 * a specified wait time.
 * 
 * 		- `timeout`: A reference to the timeout id, which is set using `setTimeout()`
 * method when calling `debounce()`. This variable stores the identifier of the timer
 * that is scheduled to execute the provided function after a specified time interval.
 * 		- `func`: The original function that was passed as an argument to `debounce()`.
 * 		- `wait`: The time interval, in milliseconds, between the function calls.
 * 		- `immediate`: A boolean value indicating whether the function should be called
 * immediately or not. If `true`, the function is called immediately after being
 * invoked; otherwise, it is scheduled to be called after the waiting period has passed.
 */
function debounce(func, wait, immediate) {
  var timeout;
  return function() {
    var context = this,
      args = arguments;
    var later = function() {
      timeout = null;
      if (!immediate) func.apply(context, args);
    };
    var callNow = immediate && !timeout;
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
    if (callNow) func.apply(context, args);
  };
}

// Toggle Sidenav
const iconNavbarSidenav = document.getElementById('iconNavbarSidenav');
const iconSidenav = document.getElementById('iconSidenav');
const sidenav = document.getElementById('sidenav-main');
let body = document.getElementsByTagName('body')[0];
let className = 'g-sidenav-pinned';

if (iconNavbarSidenav) {
  iconNavbarSidenav.addEventListener("click", toggleSidenav);
}

if (iconSidenav) {
  iconSidenav.addEventListener("click", toggleSidenav);
}

/**
 * @description Updates the class of the body and sidenav elements to reflect whether
 * the sidenav is currently open or closed.
 */
function toggleSidenav() {
  if (body.classList.contains(className)) {
    body.classList.remove(className);
    setTimeout(function() {
      sidenav.classList.remove('bg-white');
    }, 100);
    sidenav.classList.remove('bg-transparent');

  } else {
    body.classList.add(className);
    sidenav.classList.add('bg-white');
    sidenav.classList.remove('bg-transparent');
    iconSidenav.classList.remove('d-none');
  }
}

let html = document.getElementsByTagName('html')[0];

/**
 * @description Removes any class ` className` from the `body` element if it has the
 * class `g-sidenav-pinned` and is not a direct child of an element with class `sidenav-toggler-line`.
 * 
 * @param { object } e - event that triggered the function and is used to determine
 * whether to remove the 'g-sidenav-pinned' class from the body element.
 */
html.addEventListener("click", function(e) {
  if (body.classList.contains('g-sidenav-pinned') && !e.target.classList.contains('sidenav-toggler-line')) {
    body.classList.remove(className);
  }
});

// Resize navbar color depends on configurator active type of sidenav

let referenceButtons = document.querySelector('[data-class]');

window.addEventListener("resize", navbarColorOnResize);

/**
 * @description Adjusts the background color of a sidenav based on window width,
 * applying either 'bg-white' or 'bg-transparent' class to it depending on specific
 * conditions.
 */
function navbarColorOnResize() {
  if (window.innerWidth > 1200) {
    if (referenceButtons.classList.contains('active') && referenceButtons.getAttribute('data-class') === 'bg-transparent') {
      sidenav.classList.remove('bg-white');
    } else {
      if (!body.classList.contains('dark-version')) {
        sidenav.classList.add('bg-white');
      }
    }
  } else {
    sidenav.classList.add('bg-white');
    sidenav.classList.remove('bg-transparent');
  }
}

// Deactivate sidenav type buttons on resize and small screens
window.addEventListener("resize", sidenavTypeOnResize);
window.addEventListener("load", sidenavTypeOnResize);

/**
 * @description Determines whether or not to enable or disable disabled classes for
 * specific HTML elements based on window width changes, adding or removing the
 * 'disabled' class accordingly.
 */
function sidenavTypeOnResize() {
  let elements = document.querySelectorAll('[onclick="sidebarType(this)"]');
  if (window.innerWidth < 1200) {
    elements.forEach(function(el) {
      el.classList.add('disabled');
    });
  } else {
    elements.forEach(function(el) {
      el.classList.remove('disabled');
    });
  }
}


// Tabs navigation

var total = document.querySelectorAll('.nav-pills');

/**
 * @description Generates a moving tab control in an unordered list when an item is
 * hovered over. It creates a `div` element with the class `moving-tab`, sets its
 * position absolute, and adds it to the item's parent element. The `div` element has
 * a `transition` property set for ease of animation. When the item is hovered over,
 * the `div` element animates to the right position based on the length of the list
 * and the index of the hovered item.
 * 
 * @param { object } item - element to which the function is being applied, and it
 * is used to create and modify the moving tab element.
 * 
 * @param { integer } i - 0-based index of the current `li` element within its parent
 * `ul` element, which is used to determine the appropriate CSS transition and height
 * for the moving tab.
 */
total.forEach(function(item, i) {
  var moving_div = document.createElement('div');
  var first_li = item.querySelector('li:first-child .nav-link');
  var tab = first_li.cloneNode();
  tab.innerHTML = "-";

  moving_div.classList.add('moving-tab', 'position-absolute', 'nav-link');
  moving_div.appendChild(tab);
  item.appendChild(moving_div);

  var list_length = item.getElementsByTagName("li").length;

  moving_div.style.padding = '0px';
  moving_div.style.width = item.querySelector('li:nth-child(1)').offsetWidth + 'px';
  moving_div.style.transform = 'translate3d(0px, 0px, 0px)';
  moving_div.style.transition = '.5s ease';

  /**
   * @description 1) retrieves the event target, 2) gets a reference to the parent `li`
   * element, 3) loops over child elements, and 4) sets an event listener on each child
   * element to trigger a `translate3d()` animation when clicked, based on its position
   * in the list.
   * 
   * @param { object } event - triggered event, providing the necessary information for
   * the function to operate properly.
   */
  item.onmouseover = function(event) {
    let target = getEventTarget(event);
    let li = target.closest('li'); // get reference
    if (li) {
      let nodes = Array.from(li.closest('ul').children); // get array
      let index = nodes.indexOf(li) + 1;
      /**
       * @description Updates the style of an HTML element with a `.moving-tab` class, based
       * on the classification of its parent `li` element and the index of the current `li`
       * element.
       */
      item.querySelector('li:nth-child(' + index + ') .nav-link').onclick = function() {
        moving_div = item.querySelector('.moving-tab');
        let sum = 0;
        if (item.classList.contains('flex-column')) {
          for (var j = 1; j <= nodes.indexOf(li); j++) {
            sum += item.querySelector('li:nth-child(' + j + ')').offsetHeight;
          }
          moving_div.style.transform = 'translate3d(0px,' + sum + 'px, 0px)';
          moving_div.style.height = item.querySelector('li:nth-child(' + j + ')').offsetHeight;
        } else {
          for (var j = 1; j <= nodes.indexOf(li); j++) {
            sum += item.querySelector('li:nth-child(' + j + ')').offsetWidth;
          }
          moving_div.style.transform = 'translate3d(' + sum + 'px, 0px, 0px)';
          moving_div.style.width = item.querySelector('li:nth-child(' + index + ')').offsetWidth + 'px';
        }
      }
    }
  }
});


// Tabs navigation resize

/**
 * @description Updates moving tabs on resize based on their position, width, and height.
 * 
 * @param { object } event - Event object that triggered the function to be executed,
 * and it provides additional information about the event such as its type, target,
 * and more.
 */
window.addEventListener('resize', function(event) {
  /**
   * @description Removes an element with a class `moving-tab`, creates a new `div`
   * element, clones the active nav link, and appends it to the new `div`. It then adds
   * CSS styles for positioning and translating the `div` horizontally based on the
   * index of the linked element.
   * 
   * @param { HTMLDivElement. } item - element to which the moving effect will be
   * applied, and its DOM structure is accessed and manipulated within the function.
   * 
   * 		- `querySelector('.moving-tab')`: Returns an element with the class `moving-tab`.
   * 		- `querySelector(".nav-link.active")`: Returns an element with the classes
   * `nav-link` and `active`.
   * 		- `cloneNode()`: Clones the current element.
   * 		- `innerHTML` : Sets the inner HTML of the cloned element to a string.
   * 		- `classList` : Returns a set of class names associated with the element.
   * 		- `appendChild()` : Adds a new child element to the existing element.
   * 		- `style` : Gets the CSS styles defined for the element.
   * 		- `transition` : Sets the transition duration for the element.
   * 		- `li`: Gets a reference to a `<li>` element within the parent element.
   * 		- `querySelector('li:nth-child(' + j + ')')` : Returns an element with the
   * nth-child combinator and the specified value for `j`.
   * 		- `offsetHeight`/`offsetWidth` : Gets the height or width of the current element,
   * respectively.
   * 
   * 	Note that these explanations assume that `item` is a valid HTML element, and that
   * the properties/attributes mentioned above are defined for it.
   * 
   * @param { integer } i - 0-based index of the current item element being processed
   * in the loop, which is used to calculate the position and size of the moving tab.
   */
  total.forEach(function(item, i) {
    item.querySelector('.moving-tab').remove();
    var moving_div = document.createElement('div');
    var tab = item.querySelector(".nav-link.active").cloneNode();
    tab.innerHTML = "-";

    moving_div.classList.add('moving-tab', 'position-absolute', 'nav-link');
    moving_div.appendChild(tab);

    item.appendChild(moving_div);

    moving_div.style.padding = '0px';
    moving_div.style.transition = '.5s ease';

    let li = item.querySelector(".nav-link.active").parentElement;

    if (li) {
      let nodes = Array.from(li.closest('ul').children); // get array
      let index = nodes.indexOf(li) + 1;

      let sum = 0;
      if (item.classList.contains('flex-column')) {
        for (var j = 1; j <= nodes.indexOf(li); j++) {
          sum += item.querySelector('li:nth-child(' + j + ')').offsetHeight;
        }
        moving_div.style.transform = 'translate3d(0px,' + sum + 'px, 0px)';
        moving_div.style.width = item.querySelector('li:nth-child(' + index + ')').offsetWidth + 'px';
        moving_div.style.height = item.querySelector('li:nth-child(' + j + ')').offsetHeight;
      } else {
        for (var j = 1; j <= nodes.indexOf(li); j++) {
          sum += item.querySelector('li:nth-child(' + j + ')').offsetWidth;
        }
        moving_div.style.transform = 'translate3d(' + sum + 'px, 0px, 0px)';
        moving_div.style.width = item.querySelector('li:nth-child(' + index + ')').offsetWidth + 'px';

      }
    }
  });

  if (window.innerWidth < 991) {
    /**
     * @description Modifies the class attribute of a given element based on a condition.
     * It adds the `flex-column` and `on-resize` classes to the element if it does not
     * already contain them and the `classList` property is supported.
     * 
     * @param { element. } item - element being manipulated, which is assigned the classes
     * `flex-column` and `on-resize` if it does not already have these classes.
     * 
     * 		- `classList`: This property is an array of class names that can be added to the
     * element using the `add()` method.
     * 		- `contains()`: This is a method used to check if a particular class name is
     * present in the `classList`. If it does not contain the class `'flex-column'`, it
     * adds both `'flex-column'` and `'on-resize'` to its `classList` using the `add()`
     * method.
     * 
     * @param { integer } i - index of the element being processed within the array of
     * elements passed to the function.
     */
    total.forEach(function(item, i) {
      if (!item.classList.contains('flex-column')) {
        item.classList.add('flex-column', 'on-resize');
      }
    });
  } else {
    /**
     * @description Updates the `classList` of an element, removing any class that includes
     * `'on-resize'` when the element's `classList` includes `'flex-column'`.
     * 
     * @param { `HTMLDivElement`. } item - DOM element that the function is called on,
     * which the function then modifies by removing the class names 'flex-column' and
     * 'on-resize' from it.
     * 
     * 		- `classList`: A property that represents an array of class names applied to the
     * element.
     * 		- `contains()`: A method that checks whether a specified string is present in
     * the array represented by `classList`. In this context, it checks whether the string
     * `'on-resize'` is present in the array.
     * 
     * @param { integer } i - index of the element being processed in the array of elements
     * being scrolled.
     */
    total.forEach(function(item, i) {
      if (item.classList.contains('on-resize')) {
        item.classList.remove('flex-column', 'on-resize');
      }
    })
  }
});


/**
 * @description Returns the target or srcElement of an event object, if provided,
 * otherwise returns the window's event object.
 * 
 * @param { object } e - event object or a reference to it, which is used to access
 * the target element of the event.
 * 
 * @returns { HTMLElement } the event target or the src element of the event.
 * 
 * 		- `e`: The event object passed to the function as an argument, which may be
 * either a `window.event` or an arbitrary object with an `target` or `srcElement` property.
 * 		- `target`: The target element associated with the event. If `e` is an instance
 * of `Event`, then this is the element that triggered the event. Otherwise, it returns
 * `undefined`.
 * 		- `srcElement`: The source element associated with the event. This is typically
 * used when an event is bubbled up through a hierarchy of elements, and refers to
 * the element that initiated the event at its source. It returns `undefined` if `e`
 * is not an instance of `Event`.
 */
function getEventTarget(e) {
  e = e || window.event;
  return e.target || e.srcElement;
}

// End tabs navigation

// Light Mode / Dark Mode
/**
 * @description Toggles dark mode for a given element by applying CSS classes to
 * change styles, colors, and typography. It also checks if the element is checked
 * and removes the attribute when it's not.
 * 
 * @param { `HTMLInputElement` (a/an HTML input element). } el - element whose class
 * should be updated when the function is called, and it is used to call the function
 * when the user clicks on the checkbox.
 * 
 * 		- `el`: This is the deserialized input that is passed to the function. It is an
 * object with various properties and attributes.
 * 		- `checked`: If `el` has the attribute `checked`, it means that the input is
 * checked, and the value will be treated as true when passed to any nested functions.
 * 
 * 	Some of the properties of `el` are explained below:
 * 
 * 		- `classList`: This is a list of CSS class names that have been applied to the
 * element. It can contain multiple classes separated by spaces.
 * 		- `style`: This is the style attribute of the element, which contains additional
 * styling information in the form of a string.
 * 		- `href`: If `el` has an `href` attribute, it means that the element is a hyperlink
 * and can be clicked to navigate to another page or resource.
 * 		- `innerHTML`: This is the inner HTML content of the element, which can contain
 * any valid HTML content.
 * 		- `name`: If `el` has an `name` attribute, it means that the element is part of
 * a form and can send data to a server when submitted.
 * 		- `type`: This is the type of input element `el` represents, such as text,
 * checkbox, or file.
 * 		- `value`: This is the value of the input element, which can be a string or a number.
 * 
 * 	By understanding these properties and attributes, we can better work with the
 * input `el` in the `darkMode` function and make changes accordingly.
 */
function darkMode(el) {
  const body = document.getElementsByTagName('body')[0];
  const hr = document.querySelectorAll('div:not(.sidenav) > hr');
  const sidebar = document.querySelector('.sidenav');
  const sidebarWhite = document.querySelectorAll('.sidenav.bg-white');
  const hr_card = document.querySelectorAll('div:not(.bg-gradient-dark) hr');
  const text_btn = document.querySelectorAll('button:not(.btn) > .text-dark');
  const text_span = document.querySelectorAll('span.text-dark, .breadcrumb .text-dark');
  const text_span_white = document.querySelectorAll('span.text-white');
  const text_strong = document.querySelectorAll('strong.text-dark');
  const text_strong_white = document.querySelectorAll('strong.text-white');
  const text_nav_link = document.querySelectorAll('a.nav-link.text-dark');
  const secondary = document.querySelectorAll('.text-secondary');
  const bg_gray_100 = document.querySelectorAll('.bg-gray-100');
  const bg_gray_600 = document.querySelectorAll('.bg-gray-600');
  const btn_text_dark = document.querySelectorAll('.btn.btn-link.text-dark, .btn .ni.text-dark');
  const btn_text_white = document.querySelectorAll('.btn.btn-link.text-white, .btn .ni.text-white');
  const card_border = document.querySelectorAll('.card.border');
  const card_border_dark = document.querySelectorAll('.card.border.border-dark');
  const svg = document.querySelectorAll('g');
  const navbarBrand = document.querySelector('.navbar-brand-img');
  const navbarBrandImg = navbarBrand.src;
  const navLinks = document.querySelectorAll('.navbar-main .nav-link, .navbar-main .breadcrumb-item, .navbar-main .breadcrumb-item a, .navbar-main h6');
  const cardNavLinksIcons = document.querySelectorAll('.card .nav .nav-link i');
  const cardNavSpan = document.querySelectorAll('.card .nav .nav-link span');


  if (!el.getAttribute("checked")) {
    body.classList.add('dark-version');
    if (navbarBrandImg.includes('logo-ct-dark.png')) {
      var navbarBrandImgNew = navbarBrandImg.replace("logo-ct-dark", "logo-ct");
      navbarBrand.src = navbarBrandImgNew;
    }
    for (var i = 0; i < cardNavLinksIcons.length; i++) {
      if (cardNavLinksIcons[i].classList.contains('text-dark')) {
        cardNavLinksIcons[i].classList.remove('text-dark');
        cardNavLinksIcons[i].classList.add('text-white');
      }
    }
    for (var i = 0; i < cardNavSpan.length; i++) {
      if (cardNavSpan[i].classList.contains('text-sm')) {
        cardNavSpan[i].classList.add('text-white');
      }
    }
    for (var i = 0; i < hr.length; i++) {
      if (hr[i].classList.contains('dark')) {
        hr[i].classList.remove('dark');
        hr[i].classList.add('light');
      }
    }
    for (var i = 0; i < hr_card.length; i++) {
      if (hr_card[i].classList.contains('dark')) {
        hr_card[i].classList.remove('dark');
        hr_card[i].classList.add('light');
      }
    }
    for (var i = 0; i < text_btn.length; i++) {
      if (text_btn[i].classList.contains('text-dark')) {
        text_btn[i].classList.remove('text-dark');
        text_btn[i].classList.add('text-white');
      }
    }
    for (var i = 0; i < text_span.length; i++) {
      if (text_span[i].classList.contains('text-dark')) {
        text_span[i].classList.remove('text-dark');
        text_span[i].classList.add('text-white');
      }
    }
    for (var i = 0; i < text_strong.length; i++) {
      if (text_strong[i].classList.contains('text-dark')) {
        text_strong[i].classList.remove('text-dark');
        text_strong[i].classList.add('text-white');
      }
    }
    for (var i = 0; i < text_nav_link.length; i++) {
      if (text_nav_link[i].classList.contains('text-dark')) {
        text_nav_link[i].classList.remove('text-dark');
        text_nav_link[i].classList.add('text-white');
      }
    }
    for (var i = 0; i < secondary.length; i++) {
      if (secondary[i].classList.contains('text-secondary')) {
        secondary[i].classList.remove('text-secondary');
        secondary[i].classList.add('text-white');
        secondary[i].classList.add('opacity-8');
      }
    }
    for (var i = 0; i < bg_gray_100.length; i++) {
      if (bg_gray_100[i].classList.contains('bg-gray-100')) {
        bg_gray_100[i].classList.remove('bg-gray-100');
        bg_gray_100[i].classList.add('bg-gray-600');
      }
    }
    for (var i = 0; i < btn_text_dark.length; i++) {
      btn_text_dark[i].classList.remove('text-dark');
      btn_text_dark[i].classList.add('text-white');
    }
    for (var i = 0; i < sidebarWhite.length; i++) {
      sidebarWhite[i].classList.remove('bg-white');
    }
    for (var i = 0; i < svg.length; i++) {
      if (svg[i].hasAttribute('fill')) {
        svg[i].setAttribute('fill', '#fff');
      }
    }
    for (var i = 0; i < card_border.length; i++) {
      card_border[i].classList.add('border-dark');
    }
    el.setAttribute("checked", "true");
  } else {
    body.classList.remove('dark-version');
    sidebar.classList.add('bg-white');
    if (navbarBrandImg.includes('logo-ct.png')) {
      var navbarBrandImgNew = navbarBrandImg.replace("logo-ct", "logo-ct-dark");
      navbarBrand.src = navbarBrandImgNew;
    }
    for (var i = 0; i < navLinks.length; i++) {
      if (navLinks[i].classList.contains('text-dark')) {
        navLinks[i].classList.add('text-white');
        navLinks[i].classList.remove('text-dark');
      }
    }
    for (var i = 0; i < cardNavLinksIcons.length; i++) {
      if (cardNavLinksIcons[i].classList.contains('text-white')) {
        cardNavLinksIcons[i].classList.remove('text-white');
        cardNavLinksIcons[i].classList.add('text-dark');
      }
    }
    for (var i = 0; i < cardNavSpan.length; i++) {
      if (cardNavSpan[i].classList.contains('text-white')) {
        cardNavSpan[i].classList.remove('text-white');
      }
    }
    for (var i = 0; i < hr.length; i++) {
      if (hr[i].classList.contains('light')) {
        hr[i].classList.add('dark');
        hr[i].classList.remove('light');
      }
    }
    for (var i = 0; i < hr_card.length; i++) {
      if (hr_card[i].classList.contains('light')) {
        hr_card[i].classList.add('dark');
        hr_card[i].classList.remove('light');
      }
    }
    for (var i = 0; i < text_btn.length; i++) {
      if (text_btn[i].classList.contains('text-white')) {
        text_btn[i].classList.remove('text-white');
        text_btn[i].classList.add('text-dark');
      }
    }
    for (var i = 0; i < text_span_white.length; i++) {
      if (text_span_white[i].classList.contains('text-white') && !text_span_white[i].closest('.sidenav') && !text_span_white[i].closest('.card.bg-gradient-dark')) {
        text_span_white[i].classList.remove('text-white');
        text_span_white[i].classList.add('text-dark');
      }
    }
    for (var i = 0; i < text_strong_white.length; i++) {
      if (text_strong_white[i].classList.contains('text-white')) {
        text_strong_white[i].classList.remove('text-white');
        text_strong_white[i].classList.add('text-dark');
      }
    }
    for (var i = 0; i < secondary.length; i++) {
      if (secondary[i].classList.contains('text-white')) {
        secondary[i].classList.remove('text-white');
        secondary[i].classList.remove('opacity-8');
        secondary[i].classList.add('text-dark');
      }
    }
    for (var i = 0; i < bg_gray_600.length; i++) {
      if (bg_gray_600[i].classList.contains('bg-gray-600')) {
        bg_gray_600[i].classList.remove('bg-gray-600');
        bg_gray_600[i].classList.add('bg-gray-100');
      }
    }
    for (var i = 0; i < svg.length; i++) {
      if (svg[i].hasAttribute('fill')) {
        svg[i].setAttribute('fill', '#252f40');
      }
    }
    for (var i = 0; i < btn_text_white.length; i++) {
      if (!btn_text_white[i].closest('.card.bg-gradient-dark')) {
        btn_text_white[i].classList.remove('text-white');
        btn_text_white[i].classList.add('text-dark');
      }
    }
    for (var i = 0; i < card_border_dark.length; i++) {
      card_border_dark[i].classList.remove('border-dark');
    }
    el.removeAttribute("checked");
  }
}