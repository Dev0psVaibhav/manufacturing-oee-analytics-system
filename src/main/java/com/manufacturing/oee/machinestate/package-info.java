/**
 * Machine State & Telemetry Module.
 * <p>
 * Bounded context managing machine operating state streams:
 * <ul>
 *   <li>Event: High-frequency state transitions (RUNNING, UNPLANNED_DOWNTIME, PLANNED_DOWNTIME, CHANGEOVER)</li>
 *   <li>Downtime: Association of downtime root causes and micro-stop filtering</li>
 * </ul>
 * Implementation of domain entities, repositories, and services is scheduled for Phase 4+.
 */
package com.manufacturing.oee.machinestate;
