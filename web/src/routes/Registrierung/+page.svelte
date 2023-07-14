<script>
    import { FloatingLabelInput, Card, Button, Navbar, NavBrand, NavLi, NavUl, NavHamburger, Dropdown, DropdownItem, Chevron, DropdownDivider, Input } from "flowbite-svelte";
    import { goto } from '$app/navigation';

    const Register_URL = "/api/users"

    let isAuthenticated = false;

   let email, username, password;

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
        const response = await fetch(Register_URL, {
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
          if (response.status == 201) {
              console.log("200 yeah!")
              goto("/")
          } 

          /* const text = await response.text() */

          /* localStorage.setItem("jwt", text) */
       }

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
      <NavLi href="/" >Home</NavLi>
      <NavLi href="/Registrierung" active={true}>Registrierung</NavLi>
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
        <h1 class="mb-2 font-bold tracking-tight text-gray-900 dark:text-white">Registrierung</h1>
        <form action={Register_URL} on:submit|preventDefault={handleSubmit} class="flex flex-col gap-4">
            <FloatingLabelInput id="E-Mail" name="floating_standard" type="text" label="E-Mail" bind:value={email}/>
            <FloatingLabelInput id="Username" name="floating_standard" type="text" label="Username" bind:value={username}/>
            <FloatingLabelInput id="Password" name="floating_standard" type="password" label="Passwort" bind:value={password}/>
            <Button type="submit">Registrieren</Button>
        </form>
    </Card>
</div>