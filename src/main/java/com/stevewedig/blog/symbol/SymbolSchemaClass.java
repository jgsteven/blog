package com.stevewedig.blog.symbol;

import java.util.Set;

import com.google.common.collect.*;
import com.stevewedig.blog.errors.Bug;
import com.stevewedig.blog.value_objects.ValueMixin;

class SymbolSchemaClass extends ValueMixin implements SymbolSchema {

  // ===========================================================================
  // state
  // ===========================================================================

  // state
  private final ImmutableSet<Symbol<?>> requiredSymbols;
  private final ImmutableSet<Symbol<?>> optionalSymbols;

  // derived
  private final ImmutableSet<Symbol<?>> symbols;

  @Override
  public Object[] fields() {
    return array("requiredSymbols", requiredSymbols, "optionalSymbols", optionalSymbols);
  }

  // ===========================================================================
  // constructors
  // ===========================================================================

  public SymbolSchemaClass(Iterable<Symbol<?>> requiredSymbols, Iterable<Symbol<?>> optionalSymbols) {
    this.requiredSymbols = ImmutableSet.copyOf(requiredSymbols);
    this.optionalSymbols = ImmutableSet.copyOf(optionalSymbols);
    this.symbols = ImmutableSet.copyOf(Sets.union(this.requiredSymbols, this.optionalSymbols));

    validate();
  }

  private void validate() {
    
    Set<Symbol<?>> overlap = Sets.intersection(requiredSymbols, optionalSymbols);
    
    if(! overlap.isEmpty())
      throw new Bug("the symbols (%s) were passed in as both required and optional", overlap);
  }

  // ===========================================================================
  // symbols
  // ===========================================================================

  @Override
  public ImmutableSet<Symbol<?>> requiredSymbols() {
    return requiredSymbols;
  }

  @Override
  public ImmutableSet<Symbol<?>> optionalSymbols() {
    return optionalSymbols;
  }

  @Override
  public ImmutableSet<Symbol<?>> symbols() {
    return symbols;
  }

  // ===========================================================================
  // extending
  // ===========================================================================

  @Override
  public SymbolSchema withOptional(Symbol<?>... newOptionalSymbols) {

    Set<Symbol<?>> allOptionalSymbols = Sets.newHashSet(newOptionalSymbols);
    allOptionalSymbols.addAll(this.optionalSymbols);

    return SymbolLib.schema(requiredSymbols, allOptionalSymbols);
  }

  // ===========================================================================
  // validate
  // ===========================================================================

  @Override
  public void validate(Set<Symbol<?>> providedSymbols) {

    Set<Symbol<?>> missingSymbols = Sets.difference(requiredSymbols(), providedSymbols);


    Set<Symbol<?>> unexpectedSymbols = Sets.difference(providedSymbols, symbols());

    if (missingSymbols.isEmpty() && unexpectedSymbols.isEmpty())
      return;

    throw new InvalidSymbols(
        "Symbol validation failed, missing symbols = %s, unexpected symbols = %s", missingSymbols,
        unexpectedSymbols);
  }

  @Override
  public void validate(Iterable<Symbol<?>> providedSymbols) {
    validate(Sets.newHashSet(providedSymbols));
  }


  @Override
  public void validate(Symbol<?>... providedSymbols) {
    validate(Sets.newHashSet(providedSymbols));
  }


  @Override
  public void validate(SymbolMap map) {
    validate(map.symbols());
  }

}
