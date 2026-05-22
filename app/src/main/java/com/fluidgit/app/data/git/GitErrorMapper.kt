package com.fluidgit.app.data.git

import org.eclipse.jgit.api.errors.CanceledException
import org.eclipse.jgit.api.errors.CheckoutConflictException
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.api.errors.InvalidRemoteException
import org.eclipse.jgit.api.errors.TransportException
import org.eclipse.jgit.errors.NoWorkTreeException
import org.eclipse.jgit.errors.RepositoryNotFoundException
import java.io.IOException

object GitErrorMapper {
    fun mapException(exception: Exception): String {
        return when (exception) {
            is TransportException -> "Network or authentication error: ${exception.message}"
            is InvalidRemoteException -> "Invalid remote URL provided."
            is CheckoutConflictException -> "Checkout conflict. Please resolve before switching branches."
            is RepositoryNotFoundException -> "Git repository not found at the destination."
            is NoWorkTreeException -> "No work tree found for this repository."
            is CanceledException -> "Operation was canceled."
            is GitAPIException -> "Git operation failed: ${exception.message}"
            is IOException -> "File system error: ${exception.message}"
            else -> "An unexpected error occurred: ${exception.message}"
        }
    }
}
