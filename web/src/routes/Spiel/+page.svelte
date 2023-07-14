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
          <h1 class="mb-2 font-bold tracking-tight text-gray-900 dark:text-white">Hier wird ein neues Spiel angelegt</h1>
      </Card>
  </div>