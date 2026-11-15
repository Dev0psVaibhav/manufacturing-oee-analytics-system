/**
 * Audit & Traceability Module.
 * <p>
 * Bounded context providing an immutable append-only ledger for domain actions:
 * <ul>
 *   <li>Audit Logging: Actor identity, action type, and before/after JSON state snapshots</li>
 *   <li>Validation History: Log of evaluated business constraints and data reconciliation runs</li>
 * </ul>
 * Implementation of audit event interceptors and ledgers is scheduled for Phase 4+.
 */
package com.manufacturing.oee.audit;
