let sideBarOpened = false;
const sidebarElement = $("#sidebar");
const mainElement = $("#main")

const openSideBar = () => {
    mainElement.removeClass("col-md-12");
    mainElement.addClass("col-md-10");
    sidebarElement.removeClass("w-0px h-0px p-0");
}

const closeSideBar = () => {
    mainElement.addClass("col-md-12");
    mainElement.removeClass("col-md-10");
    sidebarElement.addClass("w-0px h-0px p-0");
}

$("#sideBarOpenerCloser").on("click", () => {
    if (!sideBarOpened) {
        openSideBar();
        sideBarOpened = true;
    } else {
        closeSideBar();
        sideBarOpened = false;
    }
});

$("#sideBarCloser").on("click", () => {
    closeSideBar();
    sideBarOpened = false;
});