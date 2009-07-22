package org.dcache.auth;

import java.util.Set;
import java.util.NoSuchElementException;
import javax.security.auth.Subject;
import com.sun.security.auth.UnixNumericUserPrincipal;
import com.sun.security.auth.UnixNumericGroupPrincipal;

public class Subjects
{
    /**
     * The subject representing the root user, that is, a user that is
     * empowered to do everything.
     */
    public static final Subject ROOT;

    static
    {
        ROOT = new Subject();
        ROOT.getPrincipals().add(new UnixNumericUserPrincipal(0));
        ROOT.getPrincipals().add(new UnixNumericGroupPrincipal(0, true));
        ROOT.setReadOnly();
    }

    /**
     * Returns true if and only if the subject is root, that is, has
     * the user ID 0.
     */
    public static boolean isRoot(Subject subject)
    {
        return hasUid(subject, 0);
    }

    /**
     * Returns true if and only if the subject has the given user ID.
     */
    public static boolean hasUid(Subject subject, long uid)
    {
        Set<UnixNumericUserPrincipal> principals =
            subject.getPrincipals(UnixNumericUserPrincipal.class);
        for (UnixNumericUserPrincipal principal: principals) {
            if (principal.longValue() == uid) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if and only if the subject has the given group ID.
     */
    public static boolean hasGid(Subject subject, long gid)
    {
        Set<UnixNumericGroupPrincipal> principals =
            subject.getPrincipals(UnixNumericGroupPrincipal.class);
        for (UnixNumericGroupPrincipal principal: principals) {
            if (principal.longValue() == gid) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the users IDs of a subject.
     */
    public static long[] getUids(Subject subject)
    {
        Set<UnixNumericUserPrincipal> principals =
            subject.getPrincipals(UnixNumericUserPrincipal.class);
        long[] uids = new long[principals.size()];
        int i = 0;
        for (UnixNumericUserPrincipal principal: principals) {
            uids[i++] = principal.longValue();
        }
        return uids;
    }

    /**
     * Returns the group IDs of a subject. If the user has a primary
     * group, then first element will be a primary group ID.
     */
    public static long[] getGids(Subject subject)
    {
        Set<UnixNumericGroupPrincipal> principals =
            subject.getPrincipals(UnixNumericGroupPrincipal.class);
        long[] gids = new long[principals.size()];
        int i = 0;
        for (UnixNumericGroupPrincipal principal: principals) {
            if (principal.isPrimaryGroup()) {
                gids[i++] = gids[0];
                gids[0] = principal.longValue();
            } else {
                gids[i++] = principal.longValue();
            }
        }
        return gids;
    }

    /**
     * Returns one of the primary group IDs of a subject.
     *
     * @throws NoSuchElementException if subject has no primary group
     */
    public static long getPrimaryGid(Subject subject)
        throws NoSuchElementException
    {
        Set<UnixNumericGroupPrincipal> principals =
            subject.getPrincipals(UnixNumericGroupPrincipal.class);
        for (UnixNumericGroupPrincipal principal: principals) {
            if (principal.isPrimaryGroup()) {
                return principal.longValue();
            }
        }
        throw new NoSuchElementException("Subject has no primary group");
    }
}