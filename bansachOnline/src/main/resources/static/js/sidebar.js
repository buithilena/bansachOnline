class SidebarManager {
    constructor() {
        this.sidebar = $('#sidebar');
        this.mainContent = $('#main-content');
        this.toggleButton = $('#toggleSidebar');
        this.navLinks = $('.nav-link');
        this.contentSections = $('.content-section');
        this.init();
    }

    init() {
        this.toggleButton.on('click', () => this.toggleSidebar());
        this.navLinks.on('click', (e) => this.handleNavigation(e));
        this.showSection('dashboard');
    }

    toggleSidebar() {
        this.sidebar.toggleClass('collapsed');
        this.mainContent.toggleClass('expanded');
    }

    showSection(sectionId) {
        this.contentSections.hide();
        $(`#${sectionId}`).show();
    }

    handleNavigation(e) {
        e.preventDefault();
        this.navLinks.removeClass('active');
        const link = $(e.currentTarget);
        link.addClass('active');
        const sectionId = link.attr('href').substring(1);
        this.showSection(sectionId);
    }
}
$(document).ready(function() {
    // Toggle sidebar
    $('#toggleSidebar').click(function() {
        $('#sidebar').toggleClass('collapsed');
        $('#main-content').toggleClass('expanded');
    });

    // Handle nav link clicks
    $('.nav-link').click(function(e) {
        e.preventDefault();
        $('.nav-link').removeClass('active');
        $(this).addClass('active');

        // Hide all content sections
        $('.content-section').hide();

        // Show the selected section
        const sectionId = $(this).attr('href').substring(1);
        $(`#${sectionId}`).show();
    });

    // Show dashboard by default
    $('#dashboard').show();
});

$(document).ready(() => new SidebarManager());

