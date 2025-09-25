import jwt from 'jsonwebtoken';

/**
 * Sprint-1 auth shim:
 * - If Authorization: Bearer <jwt> present, verify and use its sub as userId.
 * - Else, if DEV_FIXED_USER_ID set, use that as userId (for demos).
 * - Else 401.
 */
export function requireAuth(req, res, next) {
  const hdr = req.headers.authorization || '';
  const token = hdr.startsWith('Bearer ') ? hdr.slice(7) : null;

  if (token) {
    try {
      const payload = jwt.verify(token, process.env.JWT_SECRET || 'dev');
      req.user = { id: payload.sub || payload.userId || payload.id };
      if (!req.user.id) throw new Error('No user id in token');
      return next();
    } catch (e) {
      return res.status(401).json({ message: 'Invalid token' });
    }
  }

  // Dev fallback
  if (process.env.DEV_FIXED_USER_ID) {
    req.user = { id: process.env.DEV_FIXED_USER_ID };
    return next();
  }

  return res.status(401).json({ message: 'Unauthorized' });
}
