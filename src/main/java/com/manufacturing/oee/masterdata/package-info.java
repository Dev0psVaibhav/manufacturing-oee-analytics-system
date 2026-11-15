/**
 * Master Data Module.
 * <p>
 * Bounded context managing hierarchical manufacturing infrastructure:
 * <ul>
 *   <li>Plant: Facility definition and timezone anchor</li>
 *   <li>Line: Production lines and routing</li>
 *   <li>Machine: Workstations, ideal cycle times, and bottleneck designations</li>
 *   <li>Product: SKU catalog, standard speeds, and specifications</li>
 *   <li>Shift: Abstract shift templates, shift instances, and planned break calendars</li>
 *   <li>Downtime: Standardized downtime reason code taxonomy and loss categories</li>
 * </ul>
 * Implementation of domain entities, repositories, and services is scheduled for Phase 4+.
 */
package com.manufacturing.oee.masterdata;
