// @ts-nocheck
import { error } from '@sveltejs/kit';
import { browser } from '$app/environment';

/** */
export async function load() {
	if (!browser) {
		return;
	}

	const token = localStorage.getItem('jwt');

	if (token == null) {
		throw error(401);
	}
	const response = await fetch('/api/users', {
		method: 'GET',
		headers: {
			Authorization: `Bearer ${token}`
		}
	});

	return {
		users: await response.json()
	};
}
