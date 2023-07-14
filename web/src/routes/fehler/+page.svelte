<script>

    import { FloatingLabelInput, Card, Button, Navbar, NavBrand, NavLi, NavUl, NavHamburger, Dropdown, DropdownItem, Chevron, DropdownDivider, Input } from "flowbite-svelte";
    
    let isAuthenticated = false;

    </script>
    
    <Navbar let:hidden let:toggle>
        <NavBrand href="/">
          <img
            src="src\img\Logo.png"
            class="mr-12 h-24 sm:h-18"
            alt="Tetris Logo"
          />
          <span class="self-center whitespace-nowrap text-xl font-semibold dark:text-white">
          </span>
        </NavBrand>
        <NavHamburger on:click={toggle} />
        <NavUl {hidden}>
          <NavLi href="/" active={true}>Home</NavLi>
          <NavLi href="/Registrierung">Registrierung</NavLi>
          {#if isAuthenticated}
          <NavLi id="nav-menu1" class="cursor-pointer"><Chevron aligned>Spiel</Chevron></NavLi>
          <NavLi href="/Profil">Profil</NavLi>
          <Dropdown triggeredBy="#nav-menu1" class="w-44 z-20">
            <DropdownItem href="/Spiel">Neues Spiel</DropdownItem>
            <DropdownItem href="/Spielhistorie" >Spielhistorie</DropdownItem>
            <DropdownItem href="/OffeneSpiele" >Offene Spiele</DropdownItem>
          </Dropdown>
          {/if}
        </NavUl>
      </Navbar>
    
      <div class="container mx-auto flex items-center justify-center h-screen">
      <Card>
        <h5 class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">Da hat etwas nicht geklappt</h5>
        <p class="mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
            Klicke doch auf den Link und versuche es gleich nochmal!</p>
            <a href="/Registrierung" class="inline-flex items-center text-blue-600 hover:underline">
                Versuche es noch einmal
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5 ml-2"><path stroke-linecap="round" stroke-linejoin="round" d="M13.5 6H5.25A2.25 2.25 0 003 8.25v10.5A2.25 2.25 0 005.25 21h10.5A2.25 2.25 0 0018 18.75V10.5m-10.5 6L21 3m0 0h-5.25M21 3v5.25" /></svg>
            </a> 
      </Card>
    </div>