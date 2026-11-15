/**
 * Reproducible Export Module.
 * <p>
 * Bounded context managing synthetic dataset exports and verification digests:
 * <ul>
 *   <li>Dataset Export: CSV / JSON streaming generation for 90-day history</li>
 *   <li>Verification: Deterministic SHA-256 checksum generation matching random seed 20261022</li>
 * </ul>
 * Implementation of streaming exporters is scheduled for Phase 4+.
 */
package com.manufacturing.oee.export;
