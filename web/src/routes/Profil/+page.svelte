  <script>

  import { FloatingLabelInput, Card, Button, Navbar, NavBrand, NavLi, NavUl, NavHamburger, Dropdown, DropdownItem, Chevron, DropdownDivider, Input, Fileupload, Label, Helper } from "flowbite-svelte";
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';

    const profile_URL = "/api/users"

    let token;
      if (typeof localStorage !== 'undefined') {
        token = localStorage.getItem('jwt');
      }

      export let data;

      let isAuthenticated = true;

      let user = {};

          onMount(() => {
            const token = localStorage.getItem('jwt');
            fetchUserData(token);
          });


           function fetchUserData(token) {
            return fetch('/api/users/me', {
              method: 'GET',
              headers: {
              Authorization: 'Bearer '+ token,
              },
            })
              .then(response => {
              if (!response.ok) {
                if (response.status === 500) {
                window.location.href = '/fehler';
                return;
                }
                throw new Error(`HTTP error, status = ${response.status}`);
              }
              return response.json();
              })
              .then(data => {
              user = data;
              })
              .catch(error => {
              /* console.error(`Error: ${error}`); */
              });
            }



            /* let email, username, password;

            async function handleSubmit() {
              // const credentials = `${Email}:${username}:${password}`


                if (!email){
                  alert("Bitte eine gültige E-Mail anngeben!");
                  return;
                }
                if (!username){
                  alert("Bitte einen gültigen Username angeben!");
                  return;
                }
                if (!password){
                  alert("Bitte ein Passwort angeben!");
                  return;
                }
                const response = await fetch(profile_URL, {
                  method: 'POST',
                  headers: {
                          'Content-Type': 'application/json'
                          },
                  body: JSON.stringify({
                  email: email,
                  username: username,
                  password: password
                })
                });
                 */

/*
                const {status, ok} = response

                  // sicherlich anders schöner
                  if (response.status == 401) {
                      console.log("401")
                      goto("/fehler")
                  }
                  if (response.status == 405) {
                    console.log("405")
                    goto("/fehler")
                  }
                  if (response.status == 409) {
                    console.log("409")
                    alert("Die daten exisitieren bereits!")
                  }
                  if (response.status == 422) {
                    console.log("422")
                    alert("Die eingegeben Daten sind inkorrekt!")
                  }
                  if (response.status == 500) {
                      console.log("500")
                      goto("/fehler")
                  }
                  if (response.status == 400) {
                      console.log("400")
                      goto("/fehler")
                  }
                  if (response.status == 200) {
                      console.log("200 yeah!")
                      goto("/dashboard")
                  }

                  /* const text = await response.text() */

                  /* localStorage.setItem("jwt", text) */

/*
        async function uploadImage() {

          } */

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
      <NavLi href="/">Home</NavLi>
      <NavLi href="/Registrierung">Registrierung</NavLi>
      {#if isAuthenticated}
      <NavLi id="nav-menu1" class="cursor-pointer"><Chevron aligned>Spiel</Chevron></NavLi>
      <NavLi href="/Profil" active={true}>Profil</NavLi>
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
        <!-- <h2 class="mb-2 font-bold tracking-tight text-gray-900 dark:text-white">User ID: {user.id}</h2> -->
        <h2 class="mb-2 font-bold tracking-tight text-gray-900 dark:text-white">Username: {user?.username}</h2>
        <h2 class="mb-2 font-bold tracking-tight text-gray-900 dark:text-white">Profilbild: </h2>
        <img id="profile-picture" src={user?.profile_picture} alt="Profilbild"/>
        <h2 class="mb-2 font-bold tracking-tight text-gray-900 dark:text-white">E-Mail: {user?.email}</h2>
    </Card>
  </div>

<!-- <div class="container mx-auto flex items-center justify-center h-screen">
  <Card>
      <h1 class="mb-2 font-bold tracking-tight text-gray-900 dark:text-white">Profildaten ändern: </h1>
      <form action={profile_URL} on:submit|preventDefault={handleSubmit} class="flex flex-col gap-4">
        <FloatingLabelInput id="E-Mail" name="floating_standard" type="text" label="Username" bind:value={username}/>
        <FloatingLabelInput id="Username" name="floating_standard" type="text" label="E-Mail" bind:value={email}/>
        <FloatingLabelInput id="Password" name="floating_standard" type="password" label="Passwort" bind:value={password}/>
        <Button type="submit">Registrieren</Button>
    </form>
  </Card>
</div>

<div class= "container mx-auto flex items-center justify-center h-screen">
  <Card>
    <form action={profile_URL} on:submit|preventDefault={uploadImage} class="flex flex-col gap-4">
      <Label for="with_helper" class="pb-2">Profil ändern</Label>
      <Fileupload id="with_helper" class="mb-2" />
      <Helper>SVG, PNG, JPG or GIF (MAX. 800x400px).</Helper>
      <Button type="submit">Bild Hochladen</Button>
    </form>
  </Card>
</div>  -->