<script>
    import {
        Button,
        Card,
        Chevron,
        Dropdown,
        DropdownItem,
        FloatingLabelInput,
        Navbar,
        NavBrand,
        NavHamburger,
        NavLi,
        NavUl
    } from "flowbite-svelte";
    import {goto} from '$app/navigation';
    import {browser} from '$app/environment';


    const LOGIN_URL = "/api/login"

    let username, password;
    let isAuthenticated = false;

    if (browser) {
        const token = localStorage.getItem("jwt");

        fetch("/api/users/me", {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        }).then(response => {
            if (response.ok) {
                console.log("Hat Token")
            isAuthenticated = true;

        } else if (response.status === 401) {
                localStorage.removeItem("jwt")
            }
        })
    }

    async function handleSubmit() {
    const credentials = `${username}:${password}`;
    const response = await fetch(LOGIN_URL, {
        method: "POST",
        headers: {
            Authorization: `Basic ${btoa(credentials)}`
        }
    });

    const {status, ok} = response;

    // Handle response status
    if (response.status === 401) {
        console.log("invalid credentials");
        alert("Die daten waren leider nicht Korrekt!")
        window.location.href = window.location.href;
        window.location.reload();
    }
    if (response.status === 500) {
        console.log("Internal Server Error");
        goto("/fehler");
    }
    if (response.status === 200) {
        console.log("logged in");
        goto("/Profil");
    }

    const text = await response.text();

    localStorage.setItem("jwt", text);
}

    /* async function handleSubmit() {
        const credentials = `${username}:${password}`
        const response = await fetch(LOGIN_URL, {
            method: "POST",
            headers: {
                Authorization: `Basic ${btoa(credentials)}`
            }
        })

        // sicherlich anders schöner
        if (response.status === 401) {
            console.log("invalid credentials")
            goto("/fehler")
            return;
        }
        if (response.status === 500) {
            console.log("Internal Server Error")
            goto("/fehler")
            return;}
        if (response.status == 200) {
            console.log("eingeloggt")
            goto("/dashboard")
        }

        const text = await response.text()

        localStorage.setItem("jwt", text)
        goto("/dashboard")
    } */


  /*async function checkAuth() {
      const response = await fetch('/api/check-auth', {
          method: 'POST',
          credentials: 'include'
      });
      if (response.ok) {
          const data = await response.json();
          isAuthenticated = data.authenticated;
      } else {
          isAuthenticated = false;
      }
  }
  checkAuth(); */

</script>

<svelte:head>
    <title>Tetris</title>
</svelte:head>

<Navbar let:hidden let:toggle>
    <NavBrand href="/">
      <img
        src="src\img\Logo.png"
        class="mr-12 h-24 sm:h-18"
        alt="Tetris Logo"
      />
    </NavBrand>
    <NavHamburger on:click={toggle}/>
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
        <h1 class="mb-2 font-bold tracking-tight text-gray-900 dark:text-white">Tetris Login</h1>
        <form action={LOGIN_URL} on:submit|preventDefault={handleSubmit} class="flex flex-col gap-4">
            <FloatingLabelInput id="username" name="floating_standard" type="text" label="Username"
                                bind:value={username}/>
            <FloatingLabelInput id="password" name="floating_standard" type="password" label="Passwort"
                                bind:value={password}/>
            <Button type="submit">Login</Button>
        </form>
    </Card>
</div>

